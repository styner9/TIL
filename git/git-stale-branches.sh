#!/bin/bash

#######################################
# git-stale-branches.sh
# 
# "Stale"
# - "no one has committed to in the last three months"
# - https://help.github.com/articles/viewing-branches-in-your-repository/
#
# Reference
# - https://stackoverflow.com/a/18767922
# - https://stackoverflow.com/a/40226396
# - https://stackoverflow.com/a/7680682
# 
# TODO
# 1. check merged into ITS OWN BASE. (current 'master')
#  - https://stackoverflow.com/a/49434212 ??
# 2. support long option
#  - https://stackoverflow.com/a/7680682
#  - https://stackoverflow.com/q/12152077
#######################################


#######################################
# options
#######################################
# -d your-git-repo-dir
directory="."

# -c print | delete
default_command="print"
command=$default_command

# -o 30
min_offset=30
default_offset=90
offset=$default_offset

# -e
dry_run=true

# -m false
default_merged_only=true
merged_only=$default_merged_only


#######################################
# functions
#######################################
function show_help {
	messages=(
		""
		"[OPTIONS]"
		" -d <directory>"
		"    target git repo directory"
		"    default: ."
		" -c <command>"
		"    'print' or 'delete'"
		"    default: $default_command"
		" -o <offset>"
		"    last commit-date offset from now in days"
		"    default: $default_offset, minimum: $min_offset"
		" -e"
		"    execute (no --dry-run)"
		"    effects only with '-c delete'"
		" -m <boolean>"
		"    filter merged (into master) branches only"
		"    default: $default_merged_only"
		" -h"
		"    print this messages"
	)
	printf "%s\n" "${messages[@]}"
	exit 0
}

function print_old_branches {
	local pivot_date=$1
	local merged_only=$2
	
	local opt="-vv -r"
	# awkward test expression for safety
	if [[ "$merged_only" != "false" ]]; then
		opt="$opt --merged master"
	fi
	
	git branch $opt | egrep -v "(^\*|master|develop|test\/)" | while read; do
		local branch_name=$(echo "_$REPLY" | awk '{print $2}')
		local branch_modified=$((git log -1 --format=%ci "$branch_name" 2> /dev/null || git log -1 --format=%ci) | cut -d ' ' -f -1,4-)
		if [[ $branch_modified < $pivot_date ]]
		then 
			echo "$branch_modified $REPLY"
		fi
	done | sort
}

function delete_old_branches {
	local pivot_date=$1
	local merged_only=$2
	local dry_run=$3

	local opt=""
	# awkward test expression for safety
	if [[ $dry_run != false ]]; then
		opt="--dry-run"		
	fi
	(print_old_branches $pivot_date $merged_only) | while read; do
		local branch_name=$(echo "_$REPLY" | awk '{sub(/origin\//,"");print $2}')
		git push --delete $opt origin $branch_name
	done
}

function error {
	echo "ERROR> $1"
	exit 1
}

#######################################
# main
#######################################
# parse options
while getopts "d:c:o:em:h" arg; do
	case $arg in
		d ) directory="$OPTARG" ;;
		c ) command="$OPTARG" ;;
		o ) offset="$OPTARG" ;;
		e ) dry_run=false ;;
		m ) merged_only="$OPTARG" ;;
		h ) show_help ;;
	esac
done

if [[ ! -d $directory ]]; then
	error "invalid directory: $directory"
fi
if [[ ! "$command" =~ ^(print|delete)$ ]]; then
	error "invalid command: $command"
fi
if [[ ! "$offset" =~ ^[0-9]+$ ]]; then
	error "invalid offset: $offset"
fi
if [[ $offset -lt $min_offset ]]; then
	error "offset is too small!: $offset (should be greater than or equal to $min_offset)"
fi
if [[ ! "$merged_only" =~ ^(true|false)$ ]]; then
	echo "WARN> invalid value of -m option: $merged_only -> set to $default_merged_only (default value)"
	merged_only=default_merged_only
fi

# confirm delete w/o --dry-run
if [[ $command == "delete" && $dry_run == false ]]; then
	read -p "WARN> Are you sure to DELETE REMOTE BRANCHES? (y/n) : " -n 1 -r
	echo
	if [[ ! $REPLY =~ ^[Yy]$ ]]; then
		exit 0
	fi
fi

# run
org_dir=$(pwd)
(
	cd $directory \
	&& git fetch --prune origin \
	&& (
		pivot_date=$(date -v-${offset}d +%Y-%m-%d)
		case "$command" in
			print) print_old_branches $pivot_date $merged_only ;;
			delete) delete_old_branches $pivot_date $merged_only $dry_run ;;
		esac
	)
)
cd $org_dir

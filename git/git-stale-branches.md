## [git-stale-branches.sh](git-stale-branches.sh)

### 왜?
- 별도 훅을 걸어 놓지 않으면, 머지 후에 오래된 브랜치가 많이 남아있게 된다.
- 손으로 지우려면 귀찮다.

### 그래서?
- github branches 페이지에 "stale"로 표시된 애들 중, 안전하게 지울 수 있는 애들은 스크립트로 지워보자.

### 한 것
```bash
$ chmod +x git-stale-branches.sh
$ cp git-stale-branches.sh /path/shell/
$ export PATH="/path/shell:$PATH"
$ cd /path/my-git-repo
$ git-stale-branches.sh
```

### 배운 것

#### ["Stale Branch"](https://help.github.com/articles/viewing-branches-in-your-repository/)?
- 3달 동안 커밋이 없는 브랜치란다.
  > no one has committed to in the last three months

#### [`git branch --merged [<commit>]`](https://git-scm.com/docs/git-branch#git-branch---mergedltcommitgt)
  

- `master`에 머지된 remote 브랜치 목록은 이렇게.
  ```bash
  $ git branch -r --merged master -v 
  ```
  > Only list branches whose tips are reachable from the specified commit (HEAD if not specified)
  > ...
  > If the <commit> argument is missing it defaults to HEAD (i.e. the tip of the current branch)
  
- 이런게 있구나
  - [`merge-base`](https://git-scm.com/docs/git-merge-base)
  - [`show-branch`](https://git-scm.com/docs/git-show-branch)

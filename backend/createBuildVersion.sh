#!/usr/bin/env bash

function git_num_untracked_files {
  expr `git status --porcelain 2>/dev/null| grep "^??" | wc -l`
}

function git_num_modified_files {
    expr $((`git diff --cached --numstat | wc -l` + `git diff --numstat | wc -l`))
}

commit_hash=`git rev-parse --short HEAD`
untrackedNumber=`git_num_untracked_files`
modifiedNumber=`git_num_modified_files`
numberOfModifiedFiles=$(($untrackedNumber + $modifiedNumber))

[[ -d ./build/tmp/ ]] || mkdir -p ./build/tmp/

fullVersion="`git describe --exact-match 2>/dev/null`"

if [[ "$fullVersion" =~ ^v[0-9a-z\.\-]+$ && ${numberOfModifiedFiles} == 0 ]]; then
    version="$fullVersion-$commit_hash"
else
    timestamp=`date +"%Y%m%dT%H%M%S"`
    version="$timestamp-$commit_hash"
    if [[ ${numberOfModifiedFiles} > 0 ]]; then
        version="$version-dirty"
    fi
fi

echo "Build version $version"
echo -n "$version" > ./build/tmp/version_number
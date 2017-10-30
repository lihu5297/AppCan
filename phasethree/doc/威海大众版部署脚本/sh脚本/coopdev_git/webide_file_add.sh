#!/bin/bash
# $1 repo absolute path
# $2 file relative path

# change directory to persional repo
cd "$1"
# execute git add <file>
git add --all "$2"

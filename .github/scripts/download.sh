#!/bin/sh

if [ "$#" -ne 2 ]; then
    echo "Usage $0 <url> <dest>"
fi

url=$1
dest=$2
if [ ! -f "$dest" ]; then
  echo "Downloading $url -> $dest"
  mkdir -p "$(dirname "$dest")"
  curl -Lo "$dest".tmp "$url"
  mv "$dest".tmp "$dest"
fi

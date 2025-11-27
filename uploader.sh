#!/usr/bin/env bash
# Simple uploader: supports only -h, -t TOKEN, -d DIR
# Usage: ./upload_songs_simple.sh -t TOKEN -d /path/to/songs
set -euo pipefail

API_URL="https://chordbay.eu/api/songs"
SECTION_MARKER="# Text"
EXT="txt"

print_help() {
  cat <<EOF
Usage: $(basename "$0") -t TOKEN [-d DIR]
Options:
  -t TOKEN    API token (required)
  -d DIR      Directory with .txt song files (default: current directory)
  -h          Show this help
Example:
  $(basename "$0") -t "mytoken" -d ./songs
EOF
}

# parse args
TOKEN=""
DIR="."
while getopts "t:d:h" opt; do
  case "$opt" in
    t) TOKEN="$OPTARG" ;;
    d) DIR="$OPTARG" ;;
    h) print_help; exit 0 ;;
    *) print_help; exit 2 ;;
  esac
done

if [[ -z "$TOKEN" ]]; then
  echo "Error: token is required." >&2
  print_help
  exit 2
fi

if [[ ! -d "$DIR" ]]; then
  echo "Error: directory '$DIR' not found." >&2
  exit 3
fi

# check deps
for cmd in jq curl awk sed; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "Error: required command '$cmd' not found." >&2
    exit 4
  fi
done

shopt -s nullglob
files=("$DIR"/*."$EXT")
shopt -u nullglob

if [[ ${#files[@]} -eq 0 ]]; then
  echo "No *.$EXT files found in '$DIR'." >&2
  exit 0
fi

for file in "${files[@]}"; do
  filename=$(basename "$file" ."$EXT")
  if [[ "$filename" == *" - "* ]]; then
    artist="${filename%% - *}"
    title="${filename##* - }"
  else
    artist="Unknown Artist"
    title="$filename"
  fi

  content=$(awk -v marker="$SECTION_MARKER" '
    BEGIN{flag=0}
    {
      line=$0
      sub(/\r$/,"",line)
      tmp=line
      sub(/^[ \t]+/,"",tmp)
      sub(/[ \t]+$/,"",tmp)
      if(flag==0 && tmp==marker){ flag=1; next }
      if(flag==1){ print line }
    }' "$file" || true)

  if [[ -z "$(echo "$content" | tr -d '[:space:]')" ]]; then
    # fallback to whole file
    content=$(cat "$file")
  fi

  # trim leading/trailing whitespace
  content=$(echo "$content" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')

  if [[ -z "${content//[[:space:]]/}" ]]; then
    echo "Skipping '$file' (empty content)." >&2
    continue
  fi

  json=$(jq -n \
    --arg title "$title" \
    --arg artist "$artist" \
    --arg content "$content" \
    --argjson isPublic true \
    --argjson germanNotation true \
    '{title: $title, artist: $artist, content: $content, isPublic: $isPublic, germanNotation: $germanNotation}')

  echo "Uploading: $title — $artist"
  resp=$(curl -s -w "\n%{http_code}" -X POST "$API_URL" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer $TOKEN" \
    -d "$json")

  # split body and http code
  http_code=$(echo "$resp" | tail -n1)
  body=$(echo "$resp" | sed '$d')

  if [[ "$http_code" =~ ^2[0-9]{2}$ ]]; then
    echo "  OK (HTTP $http_code)"
  else
    echo "  Failed (HTTP $http_code):"
    echo "$body"
  fi
  echo "-----"
done

set -e

SCRIPTPATH="$( cd -- "$(dirname "$0")" >/dev/null 2>&1 ; pwd -P )/.."

if [ -z "$IMAGE" ]; then
  echo "IMAGE not set."
  exit 1
fi

if [ -z "$PLATFORM" ]; then
  echo "PLATFORM not set."
  exit 1
fi

if [ -f /.dockerenv ]; then
    runInDocker
else
  echo "Spawning docker.."
  docker run --rm --platform="$PLATFORM" --volume "$SCRIPTPATH:/build" --workdir "/build/" "$IMAGE" /build/test_scripts/"$(basename "$0")"
fi

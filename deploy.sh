#!/bin/bash
<<<<<<< HEAD

PROJECT_ROOT=$(pwd)

git checkout step

./gradlew clean build

BUILD_PATH=$(ls ${PROJECT_ROOT}/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_PATH)
=======
>>>>>>> 7f2ef0b (refactor: 배포 스크립트 수정)

# Java 설치 여부 확인 및 설치
if ! command -v java >/dev/null 2>&1; then
  sudo apt update
  sudo apt install -y openjdk-21-jdk
  echo "✅ Java 설치 완료."

else
  echo "✅ Java가 이미 설치되어 있습니다."
fi

REPO_URL="https://github.com/ming0511/spring-gift-order.git"
CLONE_DIR="$HOME/spring-gift-order"
DEPLOY_PATH="$HOME/deploy/spring-gift-order"

if [ ! -d "$CLONE_DIR" ]; then
  echo "📥 Git 클론 시작..."
  git clone "$REPO_URL" "$CLONE_DIR"
fi

cd "$CLONE_DIR"
git fetch origin
git checkout step3 || git checkout -b step3 origin/step3
git pull origin step3

echo "🛠️ 빌드 시작..."
./gradlew clean build -x test

BUILD_PATH=$(ls "${CLONE_DIR}/build/libs/"*.jar | grep -v plain | head -n 1)

if [ -z "$BUILD_PATH" ]; then
  echo "❌ 빌드된 JAR 파일을 찾을 수 없습니다."
  exit 1
fi

JAR_NAME=$(basename "$BUILD_PATH")

CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]; then
  echo "✅ 실행 중인 프로세스가 없습니다."
  sleep 1
else
  echo "🛑 기존 애플리케이션 종료 중 (PID: $CURRENT_PID)..."
  kill -15 "$CURRENT_PID"
  sleep 5
fi

<<<<<<< HEAD
DEPLOY_PATH=~/deploy/spring-gift-order

cp $BUILD_PATH $DEPLOY_PATH
cd $DEPLOY_PATH

DEPLOY_JAR=$DEPLOY_PATH/$JAR_NAME
nohup java -jar $DEPLOY_JAR > /dev/null 2> /dev/null < /dev/null &
=======
mkdir -p "$DEPLOY_PATH"
cp "$BUILD_PATH" "$DEPLOY_PATH"
cd "$DEPLOY_PATH"

DEPLOY_JAR="${DEPLOY_PATH}/${JAR_NAME}"

echo "🚀 애플리케이션 실행 시작..."
nohup java -jar "$DEPLOY_JAR" > deploy.log 2>&1 < /dev/null &

echo "🎉 배포 완료: $DEPLOY_JAR"
>>>>>>> 7f2ef0b (refactor: 배포 스크립트 수정)

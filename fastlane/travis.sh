if [[ "$TRAVIS_BRANCH" == "develop" ]]; then
  fastlane beta
  exit $?
fi

if [[ ("$TRAVIS_BRANCH" != "develop") && ("$TRAVIS_BRANCH" != "master") ]]; then
  fastlane apk_to_s3
  exit $?
fi
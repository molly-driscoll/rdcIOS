#!/bin/bash

#function push_to_sauce () {
 # FILE=$1
 # BASENAME=`basename io.billmeyer.LoanCalc`
  # echo "Pushing ${FILE} to sauce-storage:${BASENAME}"

  function push_to_sauce () {
  FILE=/Users/mollydriscoll/newrepo/rdcIOS/LoanCalc.sim.app.zip
  BASENAME=`basename LoanCalc.sim`
   echo "Pushing ${FILE} to sauce-storage:${BASENAME}"

  #curl -F 'payload=@/Users/mollydriscoll/Downloads/LoanCalc.sim.app.zip' -F name=LoanCalc.sim -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  'https://api.us-west-1.saucelabs.com/v1/storage/upload'
  #curl -F 'payload=@/Users/mollydriscoll/Downloads/Android.SauceLabs.Mobile.Sample.app.apk' -F name=sample-app-android.apk -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  'https://api.us-west-1.saucelabs.com/v1/storage/upload'

  #curl -F 'payload=@/Users/mollydriscoll/Downloads/LoanCalc.apk' -F name=LoanCalc.apk -u "$SAUCE_USERNAME:$SAUCE_ACCESS_KEY"  'https://api.us-west-1.saucelabs.com/v1/storage/upload'
  curl -u "${SAUCE_USERNAME}:${SAUCE_ACCESS_KEY}" -X POST -w "%{http_code}\n" \
       -H "Content-Type: application/octet-stream" \
       https://app.saucelabs.com/rest/v1/storage/${SAUCE_USERNAME}/${BASENAME}?overwrite=true \
        --data-binary @${FILE}

    

    

}

#push_to_sauce 'LoanCalc.ipa'
push_to_sauce 'LoanCalc.sim.app.zip'

# ZPass
ZPass is an Android app store for native web3 applications (e.g. [F-Droid](https://f-droid.org/en/packages/com.aurora.store/)). ZPass hosts verfied apps on its cloud repository where users can download and install on Android.

ZPass hosts user wallet in a secure way and enable developers' access through APIs (.aidl format). This enable developers to charge customers for app downloads and usage.

# Installation
* Open this [ZPass app](https://raw.githubusercontent.com/lushtechnology/ZPass/main/app/release/app-release.apk) download link from your mobile.
* 

or

* Install Android Studio and Sdk depending on your OS [Download link](https://developer.android.com/studio?gclid=Cj0KCQjw8e-gBhD0ARIsAJiDsaWNDdL3DzvdKx9O5QL4_bWR2k5O5rvJpIlUXccYv8JCEm_d6SWjzWcaAjMJEALw_wcB&gclsrc=aw.ds).
* Download this repostiory using git.
* Use Android Studio to run 'app'
* For the purpose of prototype, we developed a 'demo' app to show how integration with developer's app works.

# Limitations
* Android doesn't enable instalation of unsigned apps. The user has to enable it manually, but in future this could be overcome by building a custom android release with preconfigured ZPass app store.
* iOS platform doesn't allow installation of unsigned apps, so it is execluded from our scope.

# Known Issues
* Some UX Views are slow and blocking for the purpose of quick prototyping.
* XRP Payment has been develped, however xrpl4j-client (both 3.0.1 and 2.5.1 versions) package doesn't work on android. As work around we used HTTP-RPC, whcih doesn't enable all features [Stackoverflow](https://stackoverflow.com/questions/67919450/unable-to-instantiate-xrplclient-object-android-studio).

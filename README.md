# Overview
ZPass is an Android app store for native web3 applications (e.g. [F-Droid](https://f-droid.org/en/packages/com.aurora.store/)). ZPass hosts verfied apps on its cloud repository where users can download and install on Android.

ZPass hosts user wallet in a secure way and enable developers' access through APIs (.aidl format). Developers can charge customers for app downloads and usage as a new revenue stream.

Developers' apps will be host on cloud server (e.g [ZPass Store](https://github.com/lushtechnology/ZPass-store) after passing approval and KYC process. The app shows the list of available apps in Apps Tab. Then user is allowed to install the app locally and start using. Also, the app host the user wallet for payments (app purchase, service subscriptions and NFTs purchase etc..). Deverlopers will use the app APIs (IXRPAccountService.aidl) to implement various actions for customer wallet. 'Demo' module show an example of how to use this interface.

Users can navigate to Account Tab to view wallet status and can configure the wallet (address and secret). Wallet is stored in Android Preference. We currently show only XRPs, other tokens will be enabled in release app.

# Installation
* Open this [ZPass app](https://raw.githubusercontent.com/lushtechnology/ZPass/main/app/release/app-release.apk) download link from your mobile.
* Android doesn't support install apps from unknow sources by default, proceed anyway with installation.

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

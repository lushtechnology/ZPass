# ZPass
ZPass is and android app store (e.g. [F-Droid](https://f-droid.org/en/packages/com.aurora.store/)) for native web3 applications. ZPass exposes APIs (.aidl format) for native android developers like games.

# Installation
* Install Android Studio and Sdk depending on your OS [Download link](https://developer.android.com/studio?gclid=Cj0KCQjw8e-gBhD0ARIsAJiDsaWNDdL3DzvdKx9O5QL4_bWR2k5O5rvJpIlUXccYv8JCEm_d6SWjzWcaAjMJEALw_wcB&gclsrc=aw.ds).
* Download this repostiory using git.
* Use Android Studio to run 'app' and 'demo' modules on android emulator or external phone.

# Limitations
* Android doesn't enable instalation of unsigned apps. The user has to enable it manually, but in future this could be overcome by building a custom android release with preconfigured ZPass app store.
* iOS platform doesn't allow installation of unsigned apps, so it is execluded from our scope.

# Issues
xrpl4j doesn't work on android [Stackoverflow](https://stackoverflow.com/questions/67919450/unable-to-instantiate-xrplclient-object-android-studio). For the purpose of this prototype, use HTTP-RPC requests directly.

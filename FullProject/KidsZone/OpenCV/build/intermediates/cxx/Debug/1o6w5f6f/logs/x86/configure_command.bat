@echo off
"C:\\Users\\AZ\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch (GP)\\FullProject\\KidsZone\\OpenCV\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\AZ\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\AZ\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\AZ\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\AZ\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch (GP)\\FullProject\\KidsZone\\OpenCV\\build\\intermediates\\cxx\\Debug\\1o6w5f6f\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch (GP)\\FullProject\\KidsZone\\OpenCV\\build\\intermediates\\cxx\\Debug\\1o6w5f6f\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch (GP)\\FullProject\\KidsZone\\OpenCV\\.cxx\\Debug\\1o6w5f6f\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"

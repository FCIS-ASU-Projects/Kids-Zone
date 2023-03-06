@echo off
"C:\\Users\\AZ\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch\\GP_ImagePreprocessing\\openCV\\libcxx_helper" ^
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
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch\\GP_ImagePreprocessing\\openCV\\build\\intermediates\\cxx\\Debug\\3e5j413d\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch\\GP_ImagePreprocessing\\openCV\\build\\intermediates\\cxx\\Debug\\3e5j413d\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Huda\\FCIS\\4th Year\\Graduation Project\\Android Studio Repo Branch\\GP_ImagePreprocessing\\openCV\\.cxx\\Debug\\3e5j413d\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"

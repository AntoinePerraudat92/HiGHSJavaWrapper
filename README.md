# HiGHS Java Wrapper

The scope of this project is to provide a Java wrapper for the open-source [HiGHS solver](https://highs.dev/), which is
capable of solving linear (LP), mixed-integer (MIP) and quadratic programming (QP) problems.

The project relies on [SWIG](https://swig.org/) to generate the JNI classes to be able to communicate with the HiGHS
shared library file, `libhighs.so`.

Additional wrap functions can be added and help can be provided on demand.

Some examples on how to use the wrapper are provided in `src/test/java/wrapper/examples`.

## Dependencies / Prerequisites

### HiGHS

`HiGHS` must be built following [the instructions](https://github.com/ERGO-Code/HiGHS/) on its repository page. For now,
the wrapper expects version `1.15.1`.

Note that the wrapper must be compiled with the HiGHS compilation option `HIGHSINT64=on`.

### Java

A JDK 21 or later is required.

### SWIG

`SWIG` [must be installed](https://www.swig.org/) only if you intend to generate the JNI classes. If you only plan to
use the wrapper, installing `SWIG` should not be necessary.

On Ubuntu systems, one can use `sudo apt install swig`.

### Compiler

A compiler being able to compile `HiGHS` is required. For a Linux or macOS system, the environment variables `CC` or
`CXX` must be defined. Note that `HiGHS` must have been
installed with the same compiler.

## How to use the wrapper?

Firstly, `HiGHS` must be compiled, `HIGHS_HOME` and `JAVA_HOME` must be defined.

Secondly, `generate_shared_libraries_linux` must be run to build the shared libraries required by the wrapper for Linux
systems. `generate_shared_libraries_windows` can be used for Windows systems. `generate_shared_libraries_macos` can be
used for macOS systems.

The script automatically creates both required shared libraries, `libhighs.*` and `libhighswrap.so` for
Linux systems, `libhighs.dll` and `libhighswrap.dll` for
Windows systems, or `libhighs.dylib` and `libhighswrap.dylib` or macOS systems, in the base directory.

Then, to run the tests or use the wrapper for another project, the JVM argument `-Djava.library.path` must be filled.
The referred path must contain the shared libraries. The relevant classes (relying on calls to `HiGHS`)
then must also contain (or something equivalent):

```
    static {
        System.loadLibrary("highs");
        System.loadLibrary("highswrap");
    }
```

If the shared libraries cannot be found at run time, then exceptions of type
`UnsatisfiedLinkError` or type `ClassNotFound` will be thrown. Note that the load order of libraries also matters.

## Building the JNI classes

To build the JNI classes required by the wrapper, `generate_jni_classes` should be used. It builds the JNI classes in
`src/main/java/highs`. Running this script is not necessary unless you want to extend the wrapper.

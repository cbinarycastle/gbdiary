object Versions {
    private const val VERSION_MAJOR = 1
    private const val VERSION_MINOR = 0
    private const val VERSION_PATCH = 0
    const val VERSION_CODE = VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH
    const val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"

    const val COMPILE_SDK = 32
    const val TARGET_SDK = 32
    const val MIN_SDK = 21
}
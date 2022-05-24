package io.desolve.util

enum class OSType
{
    Windows,
    Unix;

    companion object
    {
        fun resolveOsType(): OSType
        {
            return when
            {
                System.getProperty("os.name").contains("Windows") -> Windows
                else -> Unix
            }
        }
    }
}
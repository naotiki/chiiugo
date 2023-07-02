enum class PreReleaseIdentifier{
    Dev,    //0 00
    Alpha,  //1 01
    Beta,   //2 10
  //Release,//3 11
}

data class AppVersion(
    val major:Int,
    val minor:Int,
    val patch:Int,

    val preRelease:PreReleaseIdentifier?,
    val preReleaseNumber:Int?

){
    init {
        checkRange(major,0..255){v,r->
            "Major version ($v) must be in the range ${r}"
        }
        checkRange(minor,0..255){v,r->
            "Minor version ($v) must be in the range ${r}"
        }
        checkRange(patch,1..255){v,r->
            "Patch version ($v) must be in the range ${r}"
        }
        if (preRelease!=null&&preReleaseNumber!=null){
            checkRange(preReleaseNumber,1..63){v,r->
                "PreRelease number ($v) must be in the range ${r}"
            }
        }
    }
    fun generateMacVersion():String{
        val winPatch=if (preRelease!=null&&preReleaseNumber!=null){
            (patch shl 8) or (preRelease.ordinal shl 6) or preReleaseNumber
        }else (patch shl 8) or (0b11 shl 6)
        return "${major+1}.$minor.$winPatch"
    }
    fun generateWindowsVersion(): String {
        val winPatch=if (preRelease!=null&&preReleaseNumber!=null){
            (patch shl 8) or (preRelease.ordinal shl 6) or preReleaseNumber
        }else (patch shl 8) or (0b11 shl 6)
        return "$major.$minor.$winPatch"
    }
    fun generateRpmVersion(): String {
        return buildString {
            append("$major.$minor.$patch")
            if (preRelease!=null&&preReleaseNumber!=null){
                append("_${preRelease.name.toLowerCase()}$preReleaseNumber")
            }
        }
    }
    fun generateDebVersion(): String {
        return buildString {
            append("$major.$minor.$patch")
            if (preRelease!=null&&preReleaseNumber!=null){
                append("-${preRelease.name.toLowerCase()}$preReleaseNumber")
            }
        }
    }

    override fun toString(): String {
        return generateDebVersion()
    }
    companion object{
        private val versionRegex=Regex(
            "v??([0-9]+).([0-9]+).([0-9]+)(-(${PreReleaseIdentifier.values().map { it.name.toLowerCase() }.joinToString("|")})([0-9]+))??"
        )

        fun parseAppVersion(versionText:String): AppVersion {
            val exception=IllegalArgumentException("Invalid Version Format.\nRegex:$versionRegex")
            val matchEntries=versionRegex.matchEntire(versionText)?:throw exception
            val major=matchEntries.groups[1]?.value?.toIntOrNull()?:throw exception
            val minor=matchEntries.groups[2]?.value?.toIntOrNull()?:throw exception
            val patch=matchEntries.groups[3]?.value?.toIntOrNull()?:throw exception
            val rev=PreReleaseIdentifier.values().singleOrNull {it.name.toLowerCase()==matchEntries.groups[5]?.value}
            val revNumber=matchEntries.groups[6]?.value?.toIntOrNull()

            return AppVersion(
                major,minor,patch,rev,revNumber
            )
        }
    }
}

fun checkRange(value: Int, range: IntRange, lazyMessage: (Int,IntRange) -> Any={ _, _ -> "Check failed." }){
    if (value !in range){
        throw IllegalStateException(lazyMessage(value,range).toString())
    }
}
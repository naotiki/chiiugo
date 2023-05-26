enum class Revision{
    Dev,
    Alpha,
    Beta,
}

data class AppVersion(
    val major:Int,
    val minor:Int,
    val patch:Int,

    val revision:Revision?,
    val revisionNumber:Int?

){
    fun generateWindowsVersion(): String {
        return "$major.$minor.$patch${revision?.ordinal?:Revision.values().size}${revisionNumber?:"00"}"
    }
    fun generateRpmVersion(): String {
        return buildString {
            append("$major.$minor.$patch")
            if (revision!=null&&revisionNumber!=null){
                append("_${revision.name.toLowerCase()}$revisionNumber")
            }
        }
    }
    fun generateDebVersion(): String {
        return buildString {
            append("$major.$minor.$patch")
            if (revision!=null&&revisionNumber!=null){
                append("-${revision.name.toLowerCase()}$revisionNumber")
            }
        }
    }

    override fun toString(): String {
        return generateDebVersion()
    }
    companion object{
        private val versionRegex=Regex(
            "v??([0-9]+).([0-9]+).([1-9]+)(-(${Revision.values().map { it.name.toLowerCase() }.joinToString("|")})([1-9]+[0-9]*))??"
        )

        fun parseAppVersion(versionText:String): AppVersion {
            val exception=IllegalArgumentException("Wrong Version Format")
            val matchEntries=versionRegex.matchEntire(versionText)?:throw exception
            val major=matchEntries.groups[1]?.value?.toIntOrNull()?:throw exception
            val minor=matchEntries.groups[2]?.value?.toIntOrNull()?:throw exception
            val patch=matchEntries.groups[3]?.value?.toIntOrNull()?:throw exception
            val rev=Revision.values().singleOrNull {it.name.toLowerCase()==matchEntries.groups[5]?.value}
            val revNumber=matchEntries.groups[6]?.value?.toIntOrNull()
            return AppVersion(
                major,minor,patch,rev,revNumber
            )
        }
    }
}
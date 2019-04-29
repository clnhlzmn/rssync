package xyz.colinholzman.rssync

class JSONResourceDescriptor {
    val expires: String? = null
    val subject: String = "subject"
    val aliases: Array<String>? = null
    val properties: Map<String, Any?>? = null
    val links: Array<Link>? = null
    class Link {
        val rel: String = "rel"
        val type: String? = null
        val href: String? = null
        val titles: Map<String, String>? = null
        val properties: Map<String, String?>? = null
    }
}


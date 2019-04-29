package xyz.colinholzman.rssync

import android.net.Uri
import android.webkit.URLUtil

class Authorization {
    companion object {

        val clientId = "colinholzman.xyz"
        val redirectUrl = Uri.parse(URLUtil.guessUrl("https://rssync.colinholzman.xyz"))
        val scope = "clipboard:rw"

        fun getHref(jrd: JSONResourceDescriptor): Uri {
            return Uri.parse(jrd.links!![0].href)
        }

        fun getAuthQuery(jrd: JSONResourceDescriptor): Uri {

            val authUrlString = jrd.links?.get(0)?.properties?.get("http://tools.ietf.org/html/rfc6749#section-4.2")
            val version = jrd.links?.get(0)?.properties?.get("http://remotestorage.io/spec/version")

            val authUri = Uri.parse(authUrlString)
            val authQuery =
                Uri.Builder()
                    .scheme(authUri.scheme)
                    .authority(authUri.authority)
                    .path(authUri.path)
                    .fragment(authUri.fragment)
                    .appendQueryParameter("client_id", clientId)
                    .appendQueryParameter("response_type", "token")
                    .appendQueryParameter("redirect_uri", redirectUrl.toString())
                    .appendQueryParameter("scope", scope)
                    .build()

            return authQuery
        }
    }
}
package xyz.colinholzman.rssync

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AuthorizeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AuthorizeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AuthorizeFragment : Fragment() {

    private class Constants {
        companion object {
            // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
            const val ARG_PARAM1 = "param1"
        }
    }

    //class to intercept the redirect url and get access token when we authorize
    private class AuthTokenWebViewClient(
        val redirectAuthority: String?,
        val listener: OnAuthorizationListener?
    ): WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (request.url.authority == redirectAuthority) {
                val token = request.url.fragment?.removePrefix("access_token=")
                if (token != null && token != request.url.fragment) {
                    listener?.onAuthorization(token)
                    return true
                }
            }
            view.loadUrl(request.url.toString())
            return false
        }
    }

    private var authUrl: String? = null
    private var listener: OnAuthorizationListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            authUrl = it.getString(Constants.ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authorize, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnAuthorizationListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnConnectedInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView = view.findViewById<WebView>(R.id.authorize_web_view)
        webView?.webViewClient =
            AuthTokenWebViewClient(Authorization.redirectUrl.authority?.toString(), listener)
        webView?.loadUrl(authUrl)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnAuthorizationListener {
        fun onAuthorization(token: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param authUrl Parameter 1.
         * @return A new instance of fragment AuthorizeFragment.
         */
        @JvmStatic
        fun newInstance(authUrl: String) =
            AuthorizeFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ARG_PARAM1, authUrl)
                }
            }
    }
}

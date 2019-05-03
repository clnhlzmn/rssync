package xyz.colinholzman.rssync

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import xyz.colinholzman.remotestorage_kotlin.RemoteStorage

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ConnectedFragment.OnConnectedInteractionListener] interface
 * to handle interaction events.
 * Use the [ConnectedFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ConnectedFragment : Fragment() {

    private var token: String? = null
    private var listener: OnConnectedInteractionListener? = null

    var rs: RemoteStorage? = null

    private class Constants {
        companion object {
            // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
            const val ARG_PARAM1 = "param1"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            token = it.getString(Constants.ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connected, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnConnectedInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnConnectedInteractionListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pushButton = view.findViewById<Button>(R.id.button_push)
        pushButton.setOnClickListener {
            val value = Clipboard.getContent(context!!)
            if (value != null) {
                rs?.put("/clipboard/txt", value,
                    { listener?.onError(it) },
                    { }
                )
            }
        }

        val pullButton = view.findViewById<Button>(R.id.button_pull)
        pullButton.setOnClickListener {
            rs?.get("/clipboard/txt",
                { listener?.onError(it) },
                { Clipboard.setContent(context!!, it) }
            )
        }

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
    interface OnConnectedInteractionListener {
        fun onError(what: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param token Parameter 1.
         * @return A new instance of fragment ConnectedFragment.
         */
        @JvmStatic
        fun newInstance(token: String) =
            ConnectedFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ARG_PARAM1, token)
                }
            }
    }
}

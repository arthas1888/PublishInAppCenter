package co.edu.aulamatriz.dbapplication.adapters

import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.view.View.OnLongClickListener
import co.edu.aulamatriz.dbapplication.R


class CustomRecyclerAdapter() :
        RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    private var myDataset: Cursor? = null


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): CustomRecyclerAdapter.ViewHolder {
        // create a new view
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_song, parent, false) as View
        // set the view's size, margins, paddings and layout parameters

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        myDataset!!.moveToPosition(position)
        holder.nameTextView!!.text = myDataset?.getString(myDataset!!.getColumnIndex("joke"))
        holder.cityTextView!!.text = myDataset?.getString(myDataset!!.getColumnIndex("categories"))

    }

    fun swap(cur: Cursor?) {
        if (cur != null)
            myDataset = cur
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        if (myDataset == null) return 0
        return myDataset!!.count
    }

    class ViewHolder(view: View)
        : RecyclerView.ViewHolder(view) {

        var mView = view
        var nameTextView: TextView? = null
        var cityTextView: TextView? = null
        var editImageView: ImageView? = null

        init {
            nameTextView = view.findViewById(R.id.nombre)
            cityTextView = view.findViewById(R.id.ciudad)
            editImageView = view.findViewById(R.id.compartir)
        }

    }
}
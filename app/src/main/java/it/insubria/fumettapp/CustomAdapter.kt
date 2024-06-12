package it.insubria.fumettapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(
    private val activity: Activity,
    private val context: Context,
    private val bookId: ArrayList<String>,
    private val bookTitle: ArrayList<String>,
    private val bookAuthor: ArrayList<String>,
    private val bookPages: ArrayList<String>
) : RecyclerView.Adapter<CustomAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.my_row, parent, false)
        return MyViewHolder(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bookIdTxt.text = bookId[position]
        holder.bookTitleTxt.text = bookTitle[position]
        holder.bookAuthorTxt.text = bookAuthor[position]
        holder.bookPagesTxt.text = bookPages[position]

        // RecyclerView onClickListener
        holder.mainLayout.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("id", bookId[position])
            intent.putExtra("title", bookTitle[position])
            intent.putExtra("author", bookAuthor[position])
            intent.putExtra("pages", bookPages[position])
            activity.startActivityForResult(intent, 1)
        }
    }

    override fun getItemCount(): Int {
        return bookId.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookIdTxt: TextView = itemView.findViewById(R.id.book_id_txt)
        val bookTitleTxt: TextView = itemView.findViewById(R.id.book_title_txt)
        val bookAuthorTxt: TextView = itemView.findViewById(R.id.book_author_txt)
        val bookPagesTxt: TextView = itemView.findViewById(R.id.book_pages_txt)
        val mainLayout: LinearLayout = itemView.findViewById(R.id.mainLayout)

        init {
            // Animate RecyclerView
            val translateAnim = AnimationUtils.loadAnimation(context, R.anim.translate_anim)
            mainLayout.animation = translateAnim
        }
    }
}


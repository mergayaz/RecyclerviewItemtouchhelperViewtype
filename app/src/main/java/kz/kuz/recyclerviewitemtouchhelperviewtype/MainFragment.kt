package kz.kuz.recyclerviewitemtouchhelperviewtype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MainFragment : Fragment() {
    private lateinit var mMainRecyclerView: RecyclerView
    private lateinit var mAdapter: MainAdapter

    private inner class Item {
        var mTitle: String? = null
        var mPart1: String? = null
        var mPart2: String? = null
        var viewType = 0
    }

    private val items: MutableList<Item> = ArrayList()

    // методы фрагмента должны быть открытыми
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        activity?.setTitle(R.string.toolbar_title)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        mMainRecyclerView = view.findViewById(R.id.main_recycler_view)
        mMainRecyclerView.layoutManager = LinearLayoutManager(activity)
        for (i in 0..19) {
            val item = Item()
            item.mTitle = "Title #" + (i + 1)
            item.mPart1 = "Part1 #" + (i + 1)
            item.mPart2 = "Part2 #" + (i + 1)
            item.viewType = 0
            items.add(item)
        }
        mAdapter = MainAdapter(items)
        mMainRecyclerView.adapter = mAdapter
        val callback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = items[viewHolder.adapterPosition]
                // получаем смахнутый экземпляр и меняем его viewType
                if (item.viewType == 0) {
                    item.viewType = 1
                } else {
                    item.viewType = 0
                }
                mAdapter.notifyDataSetChanged() // необходимо обновить весь адаптер
//                mAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                // данный вариант использовать нельзя, тогда из адаптера удаляются позиции
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(mMainRecyclerView)
        return view
    }

    private inner class MainAdapter(private val mItems: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private inner class MainHolder(view: View?) : RecyclerView.ViewHolder(view!!), View.OnClickListener {
            val mTitleTextView: TextView = itemView.findViewById(R.id.item_title)
            val mPart1TextView: TextView = itemView.findViewById(R.id.item_part1)
            val mPart2TextView: TextView = itemView.findViewById(R.id.item_part2)
            var mItem: Item? = null
            override fun onClick(view: View) {
//                Toast.makeText(activity, mItem!!.mTitle + " clicked!", Toast.LENGTH_SHORT)
//                        .show()
            mMainRecyclerView.adapter?.notifyItemMoved(adapterPosition, 0);
            mMainRecyclerView.scrollToPosition(0);
                // notifyItemMoved работает неточно при наличии другого типа представления
            }

            init {
                itemView.setOnClickListener(this) // реализуется слушатель на нажатие
            }
        }

        private inner class SecondHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            lateinit var mItem: Item
            val mButton: Button = view.findViewById(R.id.button)
            override fun onClick(view: View) {}

            init {
                mButton.setOnClickListener {
                    Toast.makeText(activity, "Button " + mItem.mTitle + " clicked!",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(activity)
            val view: View
            return if (viewType == 1) {
                view = layoutInflater.inflate(R.layout.list_item2, parent, false)
                SecondHolder(view)
            } else {
                view = layoutInflater.inflate(R.layout.list_item, parent, false)
                MainHolder(view)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = mItems[position]
            if (holder is MainHolder) {
                holder.mTitleTextView.text = item.mTitle
                holder.mPart1TextView.text = item.mPart1
                holder.mPart2TextView.text = item.mPart2
                holder.mItem = item
            } else {
                (holder as SecondHolder).mButton.text = item.mTitle
                holder.mItem = item
            }
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        override fun getItemViewType(position: Int): Int {
            return mItems[position].viewType
        }
    }

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged() // обновление в случае изменений в списке
    }
}
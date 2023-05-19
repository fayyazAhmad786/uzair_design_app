package com.uzair.landusesurvey;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter implements Filterable {

    private Context context;


    private List<String> originalData;
    private List<String> filteredData;

    private ItemFilter mFilter = new ItemFilter();

    public CustomAdapter(List<String> itemList, Context ctx) {

        originalData = new ArrayList<String>();
        filteredData = new ArrayList<String>();

        filteredData = itemList;
        originalData = itemList;

        context = ctx;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item, null);
        }

        final TextView text = (TextView) convertView.findViewById(R.id.text1);
        String textData = filteredData.get(position);
        if(null != textData)
            text.setText(filteredData.get(position));

        return convertView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<String> nlist;
            if (null != constraint){
                String filterString = constraint.toString().toLowerCase();
                final List<String> list = originalData;

                int count = list.size();
                nlist = new ArrayList(count);

                String filterableString;

                for (int i = 0; i < count; i++) {

                    filterableString = list.get(i);

                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
            } else {
                nlist = new ArrayList(1);
                nlist.add("No Data Found");
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }


}

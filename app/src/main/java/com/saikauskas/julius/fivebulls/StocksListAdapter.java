package com.saikauskas.julius.fivebulls;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StocksList;

import java.util.ArrayList;
import java.util.List;

public class StocksListAdapter extends RecyclerView.Adapter<StocksListAdapter.ViewHolder> implements Filterable {


    private static List<StocksList> stocksList;
    private static List<StocksList> filteredList;

    private static OnItemClickListner clickListener;

    public StocksListAdapter(List<StocksList> stocksList){
        this.stocksList = stocksList;
        this.filteredList = stocksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stocklistcards, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, clickListener);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //Gets current selected stock
        StocksList currentStock = filteredList.get(position);


        //holder.stockImage.setImageResource(currentStock.getStockImage());
        holder.fullName.setText(currentStock.getFullName());
        holder.symbol.setText(currentStock.getSymbol());
        holder.industry.setText(currentStock.getIndustry());
        holder.sector.setText(currentStock.getSector());





    }

    @Override
    public int getItemCount() {
        return filteredList.size();

    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String searched = charSequence.toString();
                if (searched.isEmpty()) {
                    filteredList = stocksList;

                } else {

                    ArrayList<StocksList> fileringList = new ArrayList<>();
                    for (StocksList stock : stocksList) {

                        if (stock.getSymbol().toLowerCase().contains(searched) || stock.getFullName().toLowerCase().contains(searched)
                                || stock.getSector().toLowerCase().contains(searched)){
                            fileringList.add(stock);
                        }
                    }

                    filteredList = fileringList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<StocksList>) filterResults.values;
                notifyDataSetChanged();

            }
        };

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fullName, symbol, sector, industry;

        public ViewHolder(@NonNull final View itemView, final OnItemClickListner listener) {
            super(itemView);

            fullName = itemView.findViewById(R.id.tvFullName);
            symbol = itemView.findViewById(R.id.tvSymbol);
            sector = itemView.findViewById(R.id.tvSector);
            industry = itemView.findViewById(R.id.tvIndustry);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){

                        int pos = getAdapterPosition();
                        String symbol = filteredList.get(pos).getSymbol();

                        for(int i = 0; i < stocksList.size(); i++){
                            if (symbol.equals(stocksList.get(i).getSymbol())){
                                pos = i;
                                break;
                            }
                        }

                        listener.onItemClick(pos);

                    }
                }
            });



        }

    }


    public interface OnItemClickListner {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListner listener) {
        clickListener = listener;
    }


}

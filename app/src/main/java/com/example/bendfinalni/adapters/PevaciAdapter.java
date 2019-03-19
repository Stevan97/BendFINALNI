package com.example.bendfinalni.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bendfinalni.R;
import com.example.bendfinalni.db.model.Pevaci;

import java.util.List;

public class PevaciAdapter extends BaseAdapter {

    private Context context;
    private List<Pevaci> pevaciList;

    public PevaciAdapter(Context context, List<Pevaci> pevaciList) {
        this.context = context;
        this.pevaciList = pevaciList;
    }

    private Spannable message1 = null;
    private Spannable message2 = null;

    @Override
    public int getCount() {
        return pevaciList.size();
    }

    @Override
    public Object getItem(int position) {
        return pevaciList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint({"InflateParams", "ViewHolder", "ResourceAsColor"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.pevaci_adapter, null);

        TextView naziv = convertView.findViewById(R.id.pevaci_adapter_naziv);
        message1 = new SpannableString("Naziv Pevaca: ");
        message2 = new SpannableString(pevaciList.get(position).getNazivPevaca());
        spannableStyle();
        naziv.setText(message1);
        naziv.append(message2);

        TextView zanr = convertView.findViewById(R.id.pevaci_adapter_datumRodj);
        message1 = new SpannableString("Datum Rodjenja Pevaca:  ");
        message2 = new SpannableString(pevaciList.get(position).getDatumRodj());
        spannableStyle();
        zanr.setText(message1);
        zanr.append(message2);


        RatingBar ratingBar = convertView.findViewById(R.id.pevaci_adapter_ratingBar);
        ratingBar.setRating(pevaciList.get(position).getOcenaPevaca());

        return convertView;
    }

    public void refreshList(List<Pevaci> pevaci) {
        this.pevaciList.clear();
        this.pevaciList.addAll(pevaci);
        notifyDataSetChanged();
    }

    private void spannableStyle() {
        message1.setSpan(new StyleSpan(Typeface.BOLD), 0, message1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        message2.setSpan(new ForegroundColorSpan(Color.RED), 0, message2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}

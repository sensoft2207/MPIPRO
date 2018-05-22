package com.mxi.myinnerpharmacy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mxi.myinnerpharmacy.R;
import com.mxi.myinnerpharmacy.activity.PlayPrescriptionDetail;
import com.mxi.myinnerpharmacy.model.Pausetask;
import com.mxi.myinnerpharmacy.model.PreCabinetData;
import com.mxi.myinnerpharmacy.network.CommanClass;

import java.util.ArrayList;

/**
 * Created by vishal on 23/2/18.
 */

public class PreCabinetAdapter extends RecyclerView.Adapter<PreCabinetAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    ArrayList<PreCabinetData> preCabinetlist;
    CommanClass cc;

    String videoPath,audioPath;

    public PreCabinetAdapter(Context context, ArrayList<PreCabinetData> preCabinetlist) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.preCabinetlist = preCabinetlist;
        cc = new CommanClass(context);
    }

    @Override
    public PreCabinetAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PreCabinetAdapter.MyViewHolder holder;

        View view = inflater.inflate(R.layout.pre_cabinet_item, parent, false);
        holder = new PreCabinetAdapter.MyViewHolder(view, viewType);

        return holder;

    }

    @Override
    public void onBindViewHolder(final PreCabinetAdapter.MyViewHolder holder, int position) {

        PreCabinetData pc = preCabinetlist.get(position);

        holder.tv_headline_one.setText("Prescription name :"+" "+pc.getMedia_name());
        holder.tv_headline_two.setText("Date :"+" "+pc.getDate()+" "+"Time :"+" "+pc.getTime());

        if (pc.getMedia_type().equals("Video")){

            holder.iv_video_cabinet.setImageResource(R.mipmap.vidioicon);

        }else {

            holder.iv_video_cabinet.setImageResource(R.mipmap.sound_icon);
        }

        holder.ln_cabinet_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playVideoAudio(holder.getAdapterPosition());
            }
        });

    }

    private void playVideoAudio(int adapterPosition) {

        PreCabinetData pcc = preCabinetlist.get(adapterPosition);

        videoPath = cc.loadPrefString("videoPath");
        audioPath = cc.loadPrefString("audioPath");

        if (pcc.getMedia_type().equals("Video")) {
            Intent intent = new Intent(context, PlayPrescriptionDetail.class);
            intent.putExtra("media_name", pcc.getMedia_name());
            intent.putExtra("media_type", pcc.getMedia_type());
            intent.putExtra("media_file", videoPath+"/"+  pcc.getMedia_file());
            cc.savePrefString("media_id", pcc.getMedia_id());
            context.startActivity(intent);
        } else if (pcc.getMedia_type().equals("Audio")) {
            Intent intent = new Intent(context, PlayPrescriptionDetail.class);
            intent.putExtra("media_name", pcc.getMedia_name());
            intent.putExtra("media_type", pcc.getMedia_type());
            intent.putExtra("media_file", audioPath+"/"+ pcc.getMedia_file());
            cc.savePrefString("media_id", pcc.getMedia_id());
            context.startActivity(intent);
        }else {

        }


    }

    @Override
    public int getItemCount() {
        return preCabinetlist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_headline_one,tv_headline_two;
        ImageView iv_video_cabinet;
        LinearLayout ln_cabinet_click;

        public MyViewHolder(View itemView, int ViewType) {
            super(itemView);

            ln_cabinet_click = (LinearLayout)itemView.findViewById(R.id.ln_cabinet_click);

            tv_headline_one = (TextView) itemView.findViewById(R.id.tv_headline_one);
            tv_headline_two = (TextView) itemView.findViewById(R.id.tv_headline_two);

            iv_video_cabinet = (ImageView)itemView.findViewById(R.id.iv_video_cabinet);

        }
    }

}

package com.shopi.android.receiptreader;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;

import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> parentList;
    private ArrayList<String> childList;

    public MyExpandableListAdapter(Context context, List<String> parentList, ArrayList<String> childList){
        this.context = context;
        this.parentList = parentList;
        this.childList = (ArrayList<String>) childList;
    }

    @Override
    public int getGroupCount(){
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition){
        return childList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_tutorial_menu_item, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText("Navigation Tutorial");
        textView.setTextSize(14);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_tutorial_menu_item, parent, false);

        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        System.out.println("CHILD POSITION: " + childPosition);
        System.out.println("CHILD LIST GET AT: " + childList.get(childPosition));
        textView.setText((CharSequence) childList.get(childPosition));
        textView.setTextSize(13);
        textView.setTypeface(Typeface.DEFAULT);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }



}

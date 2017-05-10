package com.scintillato.scintillatochat;

public class Ask_questions_category_list {
    private String name,tag_id;
    private boolean selected;
    public Ask_questions_category_list(String name,String tag_id) {
        // TODO Auto-generated constructor stub
        this.name=name;
        this.tag_id=tag_id;
        selected=false;
    }
    void set_selected(boolean b)
    {
        this.selected=b;
    }
    boolean get_selected()
    {
        return selected;
    }
    void setName(String name)
    {
        this.name=name;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    String get_name()
    {
        return name;
    }

}


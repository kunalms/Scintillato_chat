package com.scintillato.scintillatochat;

/**
 * Created by adikundiv on 04-01-2017.
 */

public class Category {
        String name = null;
        boolean selected = false;
        String no_followers;

        public Category(String name, boolean selected) {
            super();
            this.name = name;
            this.no_followers="0";
            this.selected = selected;
        }
        public Category(String name)
        {
            this.name=name;
            this.no_followers="1,000,000";
            this.selected= false;
        }
        public Category(String name, String no_followers)
        {
            this.name=name;
            this.no_followers=no_followers;
            this.selected=false;
        }
        public String getName() {
            return name;
        }
        public String getNo_followers() {
            String a;
            a=new String("Followers :");
            a+=no_followers;
            return a;
    }
        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelected() {
            return selected;
        }
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    public void incNo_followers(){
        no_followers+=1;
    }
}

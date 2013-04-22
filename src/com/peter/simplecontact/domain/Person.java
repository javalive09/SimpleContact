
package com.peter.simplecontact.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 跨进程传递实现 Parcelable 注：writeToParcel 和 createFromParcel的顺序要一致
 * 
 * @author Peter
 */
public class Person implements Parcelable {
    private int id;
    private String name;
    private String phone;

    public Person() {

    }

    public Person(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public Person(Integer id, String name, String phone) {
        super();
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", name=" + name + ", phone=" + phone + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将属性写入framework提供的Parcel对象
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(phone);
    }

    public static final Parcelable.Creator<Person> CREATOR = new Creator<Person>() {

        /**
         * 从Parcel获取 Person对象
         */
        @Override
        public Person createFromParcel(Parcel source) {
            Person person = new Person();
            person.id = source.readInt();
            person.name = source.readString();
            person.phone = source.readString();
            return person;
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }

    };

}

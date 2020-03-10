package com.zhao.pojo;


import org.hibernate.validator.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Past;
import java.util.Objects;

public class AcItems {

    private int id;
    @NotBlank
    @Length(max = 50,min = 1)
    private String name;
    @NotBlank
    @Length(max = 50,min = 1)
    private String author;
    @Length(max = 4)
    private String year;
    private String info;
    private String image;
    private String category;
    private String country;
    private float rating;
    private int status;
    private int count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcItems acItems = (AcItems) o;
        return Objects.equals(name, acItems.name) &&
                Objects.equals(author, acItems.author) &&
                Objects.equals(year, acItems.year) &&
                Objects.equals(info, acItems.info) &&
                Objects.equals(category, acItems.category) &&
                Objects.equals(country, acItems.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, year, info, category, country);
    }

    @Override
    public String toString() {
        return "AcItems{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", year='" + year + '\'' +
//                ", info='" + info + '\'' +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", country='" + country + '\'' +
                ", rating=" + rating +
                ", count=" + count +
                '}';
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

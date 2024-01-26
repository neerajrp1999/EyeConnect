package com.neer.eyeconnect.ui.DashBoard;

import androidx.lifecycle.ViewModel;

public class DashBoardViewModel extends ViewModel {
    String imageURI,reportId;
    String category, description, name, finderName,tag,dateLost,dateFound,itemName;
    String collectionName,userId;

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDateLost(String dateLost) {
        this.dateLost = dateLost;
    }

    public void setDateFound(String dateFound) {
        this.dateFound = dateFound;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getCollectionName() {
        return collectionName;
    }

    public String getImageURI() {
        return imageURI;
    }
    public String getCategory() {
        return category;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public String getFinderName() {
        return finderName;
    }
    public String getDateLost() {
        return dateLost;
    }
    public String getDateFound() {return dateFound;}
    public String getTag() {return tag;}
    public String getItemName(){ return itemName;}

    public DashBoardViewModel() {

    }
    public DashBoardViewModel(String imageURI, String category, String description, String name, String finderName, String tag, String dateLost, String itemName, String dateFound) {
        this.imageURI = imageURI;
        this.category = category;
        this.description = description;
        this.name = name;
        this.finderName = finderName;
        this.itemName = itemName;
        this.tag = tag;
        this.dateLost = dateLost;

        this.dateFound = dateFound;
    }

}
package com.example.chatterbox;

public class BlockedUser {

    String UserName;
    String UserId;
    String GroupEnrolled;
    String Statement1;
    String Statement2;
    String Statement3;

    public BlockedUser(){

    }

    public BlockedUser(String userName, String userId, String groupEnrolled, String statement1, String statement2, String statement3) {
        UserName = userName;
        UserId = userId;
        GroupEnrolled = groupEnrolled;
        Statement1 = statement1;
        Statement2 = statement2;
        Statement3 = statement3;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getGroupEnrolled() {
        return GroupEnrolled;
    }

    public void setGroupEnrolled(String groupEnrolled) {
        GroupEnrolled = groupEnrolled;
    }

    public String getStatement1() {
        return Statement1;
    }

    public void setStatement1(String statement1) {
        Statement1 = statement1;
    }

    public String getStatement2() {
        return Statement2;
    }

    public void setStatement2(String statement2) {
        Statement2 = statement2;
    }

    public String getStatement3() {
        return Statement3;
    }

    public void setStatement3(String statement3) {
        Statement3 = statement3;
    }
}

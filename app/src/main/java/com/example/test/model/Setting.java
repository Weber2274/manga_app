package com.example.test.model;

    public class Setting {
        private final String nickname;
        private final String email;
        private final String phone;
        private final String province;
        private final String city;
        private final String area;
        private final String points;

        public Setting(String nickname, String email, String points,String phone,String province,String city,String area) {
            this.nickname = nickname;
            this.email = email;
            this.phone = phone;
            this.province = province;
            this.city = city;
            this.area = area;
            this.points = points;

        }

        public String getNickname() { return nickname; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getProvince(){return province;}
        public String getCity(){return city;}
        public String getArea(){return area;}
        public String getPoints() { return points; }
    }



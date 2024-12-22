package com.tcs.profileservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserdetailRepository extends JpaRepository<Userdetail, String> {

    @Transactional
    @Modifying
    @Query("""
            update Userdetail u set u.firstname = ?1, u.lastname = ?2, u.email = ?3, u.phone = ?4, u.city = ?5, u.country = ?6
            where u.username = ?7""")
    int updateFirstnameAndLastnameAndEmailAndPhoneAndCityAndCountryByUsername(String firstname, String lastname, String email, String phone, String city, String country, String username);
}
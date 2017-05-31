package com.clinic.controller;

import com.clinic.SessionDB;
import com.clinic.model.Credential;
import com.clinic.model.DoctorEntity;
import com.clinic.model.UserEntity;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AuthenticationController
{
    private org.hibernate.Session session;

    @RequestMapping(value = "/api/authenticate", method = RequestMethod.POST)
    public synchronized Map<String, String> attemptLogin(@RequestBody Map<String, String> credentials)
    {
        session = SessionDB.getSession();

        try
        {
            Credential testUser = new Credential(credentials.get("username"), credentials.get("password"));

            String statement = "FROM UserEntity WHERE username = :username";
            Query query = session.createQuery(statement).setParameter("username", credentials.get("username"));
            UserEntity user = (UserEntity) query.list().get(0);
            Credential dbUser = new Credential(user.getUsername(), user.getPassword());
            if(testUser.equals(dbUser))
            {
	            Map<String, String> map = new HashMap<>(1);
	            map.put("userType", user.getType());
	            if("doctor".equals(user.getType()))
	            {
		        	String stm = "FROM DoctorEntity WHERE userId = :userID";
		            Query q = session.createQuery(stm).setParameter("userID", user.getUserId());
		            DoctorEntity doctor = (DoctorEntity) q.list().get(0);
		            map.put("doctorID", "" + doctor.getDoctorId());
	            }
                map.put("userID", "" + user.getUserId());
	            session.close();
                return map;
            }
        } catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        session.close();
        return null;
    }
}

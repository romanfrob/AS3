package com.clinic.controller;

import com.clinic.SessionDB;
import com.clinic.model.DoctorEntity;
import com.clinic.model.UserEntity;
import org.hibernate.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
public class UserController
{
	private org.hibernate.Session session;

	@RequestMapping(value = "/api/user/add", method = RequestMethod.POST)
	public synchronized void addUser(@RequestBody Map<String, String> userMap)
	{
		session = SessionDB.getSession();

		UserEntity user = new UserEntity();
		user.setUsername(userMap.get("username"));
		user.setPassword(userMap.get("password"));
		user.setType(userMap.get("type"));

		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();

		if("doctor".equals(userMap.get("type")))
		{
			DoctorEntity doctor = new DoctorEntity();
			doctor.setAvailability("free");
			doctor.setDoctorName(userMap.get("doctorName"));
			doctor.setDoctorUser(user);
			session.beginTransaction();
			session.save(doctor);
			session.getTransaction().commit();
		}
		session.close();
	}

	@RequestMapping(value = "/api/user/update", method = RequestMethod.PUT)
	public synchronized void updateUser(@RequestBody Map<String, String> userMap)
	{
		session = SessionDB.getSession();

		UserEntity user = (UserEntity) session.load(UserEntity.class, Integer.parseInt(userMap.get("userID")));
		user.setUsername(userMap.get("username"));
		user.setPassword(userMap.get("password"));
		user.setType(userMap.get("type"));

		session.beginTransaction();
		session.update(user);
		session.getTransaction().commit();
		session.close();
	}

	@RequestMapping(value = "/api/user/remove", method = RequestMethod.DELETE)
	public synchronized void removeUser(@RequestParam(value = "userID") int userID)
	{
		session = SessionDB.getSession();

		UserEntity user = (UserEntity) session.load(UserEntity.class, userID);
		session.beginTransaction();
		session.delete(user);
		session.getTransaction().commit();
		session.close();
	}

	@RequestMapping(value = "/api/user/all", method = RequestMethod.GET)
	public synchronized List<UserEntity> getAllUsers()
	{
		session = SessionDB.getSession();

		String stm = "FROM UserEntity";
		Query query = session.createQuery(stm);
		@SuppressWarnings("unchecked")
		List<UserEntity> users = query.list();

		session.close();

		return users;
	}
}

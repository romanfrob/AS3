package com.clinic.controller;

import com.clinic.SessionDB;
import com.clinic.model.Consult;
import com.clinic.model.ConsultationEntity;
import com.clinic.model.DoctorEntity;
import com.clinic.model.PatientEntity;
import org.hibernate.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
public class DoctorController
{
	private org.hibernate.Session session;

	@RequestMapping(value = "/api/doctor/status", method = RequestMethod.GET)
	public synchronized List<DoctorEntity> getDoctorByStatus(@RequestParam(value = "status") String status)
	{
		session = SessionDB.getSession();

		String stm = "FROM DoctorEntity WHERE availability = :status";
		Query query = session.createQuery(stm).setString("status", status);
		@SuppressWarnings("unchecked")
		List<DoctorEntity> doctors = query.list();
		session.close();
		return doctors;
	}

	@RequestMapping(value = "/api/doctor/all", method = RequestMethod.GET)
	public synchronized List<DoctorEntity> getAllDoctors()
	{
		session = SessionDB.getSession();

		String stm = "FROM DoctorEntity";
		Query query = session.createQuery(stm);
		@SuppressWarnings("unchecked")
		List<DoctorEntity> doctors = query.list();
		session.close();
		return doctors;
	}

	@RequestMapping(value = "/api/doctor/time", method = RequestMethod.GET)
	public synchronized List<DoctorEntity> getDoctorsForTime(@RequestParam(value = "moment") String moment)
	{
		session = SessionDB.getSession();

		String stm = "FROM ConsultationEntity WHERE since <= :time AND till >= :time";
		Timestamp sinceDate = new Timestamp(Long.parseLong(moment) * 1000);
		Query query = session.createQuery(stm).setString("time", "" + sinceDate);
		@SuppressWarnings("unchecked")
		List<ConsultationEntity> consults = query.list();
		stm = "FROM DoctorEntity";
		query = session.createQuery(stm);
		@SuppressWarnings("unchecked")
		List<DoctorEntity> doctors = query.list();
		for(ConsultationEntity c : consults)
		{
			doctors.remove(c.getDoctorByDoctorId());
		}
		session.close();
		return doctors;
	}

	@RequestMapping(value = "/api/doctor/check", method = RequestMethod.GET)
	public synchronized int getAvailabilityForInterval(@RequestParam(value = "time") String time, @RequestParam(value = "doctorID") int doctorID)
	{
		session = SessionDB.getSession();
		DoctorEntity doctor = (DoctorEntity) session.load(DoctorEntity.class, doctorID);
		Timestamp sinceDate = new Timestamp(Long.parseLong(time) * 1000);
		String stm = "FROM ConsultationEntity WHERE since <= :time AND till >= :time AND doctorByDoctorId = :doctor";
		Query query = session.createQuery(stm).setString("time", "" + sinceDate).setParameter("doctor", doctor);
		if(query.list().size() > 0)
		{
			session.close();
			return 0;
		}
		session.close();
		return 1;
	}

	@RequestMapping(value = "/api/doctor/notification", method = RequestMethod.GET)
	public synchronized Consult getCurrentPatient(@RequestParam(value = "doctorID") int doctorID)
	{
		session = SessionDB.getSession();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		DoctorEntity doctor = (DoctorEntity) session.load(DoctorEntity.class, doctorID);
		String statement = "FROM ConsultationEntity WHERE since <= :time AND till >= :time AND doctorByDoctorId = :doctor AND status = :status";
		Query query = session.createQuery(statement).setString("time", "" + now).setParameter("doctor", doctor).setString("status", "ongoing");

		List<ConsultationEntity> currentConsults = query.list();
		if(currentConsults.size() > 0)
			return new Consult(currentConsults.get(0));
		return null;
	}
}

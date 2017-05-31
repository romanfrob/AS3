package com.clinic.controller;

import com.clinic.SessionDB;
import com.clinic.model.PatientEntity;
import org.hibernate.Query;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


@RestController
public class PatientController
{
	private org.hibernate.Session session;

	@RequestMapping(value = "/api/patient/add", method = RequestMethod.POST)
	public synchronized void addPatient(@RequestBody Map<String, String> patientMap)
	{
		session = SessionDB.getSession();

		PatientEntity patient = new PatientEntity();
		patient.setName(patientMap.get("name"));
		patient.setAddress(patientMap.get("address"));
		patient.setBirthDate(new Timestamp(Long.parseLong(patientMap.get("birthDate"))));
		patient.setCnp(patientMap.get("cnp"));

		session.beginTransaction();
		session.save(patient);
		session.getTransaction().commit();
		session.close();
	}

	@RequestMapping(value = "/api/patient/update", method = RequestMethod.POST)
	public synchronized void updatePatient(@RequestBody Map<String, String> patientMap)
	{
		session = SessionDB.getSession();

		PatientEntity patient = (PatientEntity) session.load(PatientEntity.class, Integer.parseInt(patientMap.get("patientID")));
		patient.setName(patientMap.get("name"));
		patient.setAddress(patientMap.get("address"));
		patient.setBirthDate(new Timestamp(Long.parseLong(patientMap.get("birthDate"))));
		patient.setCnp(patientMap.get("cnp"));

		session.beginTransaction();
		session.update(patient);
		session.getTransaction().commit();
		session.close();
	}

	@RequestMapping(value = "/api/patient/remove", method = RequestMethod.DELETE)
	public synchronized void removePatient(@RequestParam(value = "patientID") int patientID)
	{
		session = SessionDB.getSession();

		PatientEntity patient = (PatientEntity) session.load(PatientEntity.class, patientID);
		session.beginTransaction();
		session.delete(patient);
		session.getTransaction().commit();
		session.close();
	}

	@RequestMapping(value = "/api/patient/all", method = RequestMethod.GET)
	public synchronized List<PatientEntity> getAllPatients()
	{
		session = SessionDB.getSession();

		String stm = "FROM PatientEntity";
		Query query = session.createQuery(stm);
		@SuppressWarnings("unchecked")
		List<PatientEntity> patients = query.list();

		session.close();

		return patients;
	}
}

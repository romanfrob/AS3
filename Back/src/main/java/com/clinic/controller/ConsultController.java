package com.clinic.controller;

import com.clinic.SessionDB;
import com.clinic.model.Consult;
import com.clinic.model.ConsultationEntity;
import com.clinic.model.DoctorEntity;
import com.clinic.model.PatientEntity;
import com.clinic.websocket.NotificationController;
import org.hibernate.Query;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
public class ConsultController
{
    private org.hibernate.Session session;

	@RequestMapping(value = "/api/consult/add", method = RequestMethod.POST)
	public synchronized int addConsult(@RequestBody Map<String, String> consultMap)
	{
		session = SessionDB.getSession();

		ConsultationEntity newConsult = new ConsultationEntity();
		newConsult.setSince(new Timestamp(Long.parseLong(consultMap.get("since"))));
		DoctorEntity doctor = (DoctorEntity) session.load(DoctorEntity.class, Integer.parseInt(consultMap.get("doctorID")));
		newConsult.setDoctorByDoctorId(doctor);

		String stm = "FROM PatientEntity WHERE cnp = :cnp";
		Query query = session.createQuery(stm).setParameter("cnp", consultMap.get("cnp"));
		PatientEntity patient;
		if(query.list().size() == 0)
		{
			patient = new PatientEntity();
			patient.setCnp(consultMap.get("cnp"));
			patient.setName(consultMap.get("name"));
			patient.setBirthDate(new Timestamp(System.currentTimeMillis()));
			session.beginTransaction();
			session.save(patient);
			session.getTransaction().commit();
		} else {
			patient = (PatientEntity) query.list().get(0);
		}

		newConsult.setPatientByPatientId(patient);
		if(consultMap.containsKey("till"))
		{
			newConsult.setTill(new Timestamp(Long.parseLong(consultMap.get("till"))));
		}
		if(consultMap.containsKey("status"))
		{
			newConsult.setStatus(consultMap.get("status"));
		} else {
			newConsult.setStatus("upcoming");
		}
		if(consultMap.containsKey("description"))
		{
			newConsult.setDescription(consultMap.get("description"));
		}

		session.beginTransaction();
		session.save(newConsult);
		session.getTransaction().commit();
		session.close();

		return 0;
	}

	@RequestMapping(value = "/api/consult/update", method = RequestMethod.PUT)
	public synchronized int updateConsult(@RequestBody Map<String, String> consultMap)
	{
		session = SessionDB.getSession();

		ConsultationEntity consult = (ConsultationEntity) session.load(ConsultationEntity.class, Integer.parseInt(consultMap.get("consultID")));
		if(consultMap.containsKey("since"))
			consult.setSince(new Timestamp(Long.parseLong(consultMap.get("since"))));
		DoctorEntity doctor = (DoctorEntity) session.load(DoctorEntity.class, Integer.parseInt(consultMap.get("doctorID")));
		consult.setDoctorByDoctorId(doctor);

		if(consultMap.containsKey("patientID"))
		{
			PatientEntity patient;
			patient = (PatientEntity) session.load(PatientEntity.class, Integer.parseInt(consultMap.get("patientID")));
			consult.setPatientByPatientId(patient);
		}

		if(consultMap.containsKey("till"))
			consult.setTill(new Timestamp(Long.parseLong(consultMap.get("till"))));
		if(consultMap.containsKey("status"))
		{
			consult.setStatus(consultMap.get("status"));
		}

		if(consultMap.containsKey("description"))
		{
			consult.setDescription(consultMap.get("description"));
		}

		session.beginTransaction();
		session.update(consult);
		session.getTransaction().commit();
		session.close();

		return 0;
	}

	@RequestMapping(value = "/api/consult/remove", method = RequestMethod.DELETE)
	public synchronized void removeConsult(@RequestParam(value = "consultID") int consultID)
	{
		session = SessionDB.getSession();

		ConsultationEntity consult = (ConsultationEntity) session.load(ConsultController.class, consultID);
		session.beginTransaction();
		session.delete(consult);
		session.getTransaction().commit();
		session.close();
	}

	@RequestMapping(value = "/api/consult/all", method = RequestMethod.GET)
	public synchronized List<Consult> getAllConsults()
	{
		session = SessionDB.getSession();

		String stm = "FROM ConsultationEntity";
		Query query = session.createQuery(stm);
		@SuppressWarnings("unchecked")
		List<ConsultationEntity> consults = query.list();
		List<Consult> extConsults = new ArrayList<>(consults.size());
		for (ConsultationEntity c: consults)
		{
			Consult extC = new Consult(c);
			extConsults.add(extC);
		}

		session.close();

		return extConsults;
	}

	@RequestMapping(value = "/api/consult/patient", method = RequestMethod.GET)
	public synchronized List<Consult> getConsultsForPatient(@RequestParam(value = "cnp") String cnp)
	{
		session = SessionDB.getSession();

		String stm = "FROM PatientEntity WHERE cnp = :passedCNP";
		Query query = session.createQuery(stm).setString("passedCNP", cnp);
		PatientEntity patient = (PatientEntity) query.list().get(0);

		stm = "FROM ConsultationEntity WHERE patientByPatientId = :patient";
		query = session.createQuery(stm).setParameter("patient", patient);
		@SuppressWarnings("unchecked")
		List<ConsultationEntity> consults = query.list();
		List<Consult> extConsults = new ArrayList<>(consults.size());
		for (ConsultationEntity c: consults)
		{
			Consult extC = new Consult(c);
			extConsults.add(extC);
		}

		session.close();

		return extConsults;
	}

	@RequestMapping(value = "/api/consult/doctor", method = RequestMethod.GET)
	public synchronized List<Consult> getConsultsForDoctor(@RequestParam(value = "doctorID") int doctorID)
	{
		session = SessionDB.getSession();

		DoctorEntity doctor = (DoctorEntity) session.load(DoctorEntity.class, doctorID);
		String stm = "FROM ConsultationEntity WHERE doctorByDoctorId = :doctor";
		Query query = session.createQuery(stm).setParameter("doctor", doctor);
		@SuppressWarnings("unchecked")
		List<ConsultationEntity> consults = query.list();
		List<Consult> extConsults = new ArrayList<>(consults.size());
		for (ConsultationEntity c: consults)
		{
			Consult extC = new Consult(c);
			extConsults.add(extC);
		}

		session.close();

		return extConsults;
	}

	@RequestMapping(value = "/api/consult/check-in", method = RequestMethod.PUT)
	public synchronized void checkIn(@RequestBody Map<String, String> map)
	{
		int consultID = Integer.parseInt(map.get("consultID"));
		map.put("status", "ongoing");
		map.put("since", "" + (System.currentTimeMillis()));
		if(consultID == 0)
		{
			this.addConsult(map);
		} else {
			this.updateConsult(map);
		}

		this.toggleAvailability(Integer.parseInt(map.get("doctorID")));

		NotificationController notification = new NotificationController();
		notification.processDoctorNotif("w");
	}

	@RequestMapping(value = "/api/consult/check-out", method = RequestMethod.PUT)
	public synchronized void checkOut(@RequestBody Map<String, String> map)
	{
		map.put("till", "" + (System.currentTimeMillis()));
		map.put("status", "closed");
		this.toggleAvailability(Integer.parseInt(map.get("doctorID")));
		this.updateConsult(map);
	}

	private void toggleAvailability(int doctorID)
	{
		session = SessionDB.getSession();
		DoctorEntity doctor = (DoctorEntity) session.load(DoctorEntity.class, doctorID);
		if("free".equals(doctor.getAvailability()))
			doctor.setAvailability("busy");
		else
			doctor.setAvailability("free");
		session.beginTransaction();
		session.update(doctor);
		session.getTransaction().commit();
		session.close();
	}
}

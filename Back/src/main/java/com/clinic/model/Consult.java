package com.clinic.model;

import com.clinic.SessionDB;
import org.hibernate.Query;

import java.sql.Timestamp;


public class Consult
{
	private int consultID;
	private String patientName;
	private int patientID;
	private String doctorName;
	private int doctorID;
	private Timestamp since;
	private Timestamp till;
	private String status;
	private String description;

	public Consult() {}

	public Consult(ConsultationEntity entity)
	{
		this.consultID = entity.getConsultId();
		this.patientID = entity.getPatientByPatientId().getPatientId();
		this.doctorID = entity.getDoctorByDoctorId().getDoctorId();
		this.since = entity.getSince();
		this.till = entity.getTill();
		this.status = entity.getStatus();
		this.description = entity.getDescription();

		org.hibernate.Session session = SessionDB.getSession();
		String stm = "SELECT name FROM PatientEntity WHERE patientId = :id";
		Query query = session.createQuery(stm).setString("id", patientID + "");
		this.patientName = query.list().get(0).toString();

		stm = "SELECT doctorName FROM DoctorEntity WHERE doctorId = :id";
		query = session.createQuery(stm).setString("id", doctorID + "");
		this.doctorName = query.list().get(0).toString();

		session.close();
	}

	public int getConsultID()
	{
		return consultID;
	}

	public void setConsultID(int consultID)
	{
		this.consultID = consultID;
	}

	public String getPatientName()
	{
		return patientName;
	}

	public void setPatientName(String patientName)
	{
		this.patientName = patientName;
	}

	public int getPatientID()
	{
		return patientID;
	}

	public void setPatientID(int patientID)
	{
		this.patientID = patientID;
	}

	public String getDoctorName()
	{
		return doctorName;
	}

	public void setDoctorName(String doctorName)
	{
		this.doctorName = doctorName;
	}

	public int getDoctorID()
	{
		return doctorID;
	}

	public void setDoctorID(int doctorID)
	{
		this.doctorID = doctorID;
	}

	public Timestamp getSince()
	{
		return since;
	}

	public void setSince(Timestamp since)
	{
		this.since = since;
	}

	public Timestamp getTill()
	{
		return till;
	}

	public void setTill(Timestamp till)
	{
		this.till = till;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
}

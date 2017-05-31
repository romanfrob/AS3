package com.clinic.model;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.sql.Timestamp;


public class ConsultationEntity
{
    private int consultId;
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp since;
	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp till;
    private DoctorEntity doctorByDoctorId;
    private PatientEntity patientByPatientId;
	private String description;
	private String status;

    public int getConsultId()
    {
        return consultId;
    }

    public void setConsultId(int consultId)
    {
        this.consultId = consultId;
    }

    public Timestamp getSince()
    {
        return since;
    }

    public void setSince(Timestamp from)
    {
        this.since = from;
    }

    public Timestamp getTill()
    {
        return till;
    }

    public void setTill(Timestamp to)
    {
        this.till = to;
    }

	@ManyToOne
	@JoinColumn(name = "doctorID")
	public DoctorEntity getDoctorByDoctorId()
    {
        return doctorByDoctorId;
    }

    public void setDoctorByDoctorId(DoctorEntity doctorByDoctorId)
    {
        this.doctorByDoctorId = doctorByDoctorId;
    }

	@ManyToOne
	@JoinColumn(name = "patientID")
	public PatientEntity getPatientByPatientId()
    {
        return patientByPatientId;
    }

    public void setPatientByPatientId(PatientEntity patientByPatientId)
    {
        this.patientByPatientId = patientByPatientId;
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
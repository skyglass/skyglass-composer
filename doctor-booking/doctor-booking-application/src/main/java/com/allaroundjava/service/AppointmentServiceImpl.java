package com.allaroundjava.service;

import com.allaroundjava.dao.AppointmentDao;
import com.allaroundjava.dao.AppointmentSlotDao;
import com.allaroundjava.exception.NotFoundException;
import com.allaroundjava.model.Appointment;
import com.allaroundjava.model.AppointmentSlot;
import com.allaroundjava.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private static final String NOT_FOUND = "Appointment Slot not found";
    private final AppointmentDao appointmentDao;
    private final AppointmentSlotDao appointmentSlotDao;

    @Autowired
    public AppointmentServiceImpl(AppointmentDao appointmentDao, AppointmentSlotDao appointmentSlotDao) {
        this.appointmentDao = appointmentDao;
        this.appointmentSlotDao = appointmentSlotDao;
    }

    @Override
    public Appointment createAppointment(Patient patient, AppointmentSlot appointmentSlot) {
        log.debug("Creating appointment for Appointment Slot[id={}] and patient[id={}] at {} to {}", appointmentSlot.getId(),
                patient.getId(), appointmentSlot.getStartTime(), appointmentSlot.getEndTime());
        AppointmentSlot slot = appointmentSlotDao.getAvailableById(appointmentSlot.getId()).orElseThrow(() -> new NotFoundException(NOT_FOUND));
        slot.setDeleted(true);
        Appointment appointment = new Appointment(slot, patient);
        appointmentDao.persist(appointment);
        appointmentSlotDao.persist(slot);
        return appointment;
    }
}

package com.company.enroller.persistence;

import java.util.Collection;

import com.company.enroller.model.Participant;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@Component("meetingService")
public class MeetingService {

    Session session;

    public MeetingService() {
        session = DatabaseConnector.getInstance().getSession();
    }

    public Collection<Meeting> getAll() {
        return session.createCriteria(Meeting.class).list();
    }

    public Meeting findByIdMeeting(Long id) {
        return (Meeting) session.get(Meeting.class, id);
    }

    public Meeting add(Meeting meeting) {
        Transaction transaction = this.session.beginTransaction();
        session.save(meeting);
        transaction.commit();

        return meeting;
    }

    public Collection<Participant> getEnrolled(long id) {
        Meeting meeting = findByIdMeeting(id);
        return meeting.getParticipants();
    }

    public Meeting enrollParticipantToMeeting(long id, Participant participant) {
        Transaction transaction = this.session.beginTransaction();
        Meeting meeting = findByIdMeeting(id);
        meeting.addParticipant(participant);
        session.merge(meeting);
        transaction.commit();

        return meeting;
    }
}

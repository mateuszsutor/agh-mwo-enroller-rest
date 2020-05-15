package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Created by Mateusz Sutor on 14/05/2020, 22:20
 */

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @Autowired
    ParticipantService participantService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findByIdMeeting(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createNewMeeting(@RequestBody Meeting meeting) {
        Meeting foundMeeting = meetingService.findByIdMeeting(meeting.getId());
        if (foundMeeting != null) {
            return new ResponseEntity<>("Unable to register. Meeting with login "
                    + meeting.getId()
                    + " already exists", HttpStatus.CONFLICT);
        }
        meetingService.add(meeting);
        return new ResponseEntity<>(meeting, HttpStatus.CREATED);
    }

    @RequestMapping(value = "{id}/participants", method = RequestMethod.GET)
    public ResponseEntity<?> getParticipants(@PathVariable("id") Long id) {

        if (meetingService.findByIdMeeting(id) == null) {
            return new ResponseEntity("Meeting with id " + id + " does not exist." , HttpStatus.NOT_FOUND );
        } else {
            Collection<Participant> participants = meetingService.getEnrolled(id);
            return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.ACCEPTED);
        }
    }


    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long id, @RequestBody Participant enrolledParticipant) {
        Participant participant = participantService.findByLogin(enrolledParticipant.getLogin());
        if (participant == null) {
            return new ResponseEntity("A participant with login " + enrolledParticipant.getLogin() + " does not exist.",
                    HttpStatus.NOT_FOUND);
        } else {
            meetingService.enrollParticipantToMeeting(id, participant);
            Collection<Participant> participants = meetingService.getEnrolled(id);
            return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMeeting(@PathVariable("id") Long id) {
        Meeting meeting = meetingService.findByIdMeeting(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meetingService.delete(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(
            @PathVariable("id") Long id,
            @RequestBody Meeting updatedMeeting) {

        Meeting foundMeeting = meetingService.findByIdMeeting(id);
        if (foundMeeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            if (updatedMeeting.getTitle() != null && !updatedMeeting.getTitle().equals("")
                    && !updatedMeeting.getTitle().equals(foundMeeting.getTitle())) {
                foundMeeting.setTitle(updatedMeeting.getTitle());
            } else {
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
        }
        meetingService.update(foundMeeting);
        return new ResponseEntity(foundMeeting, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/participants", method = RequestMethod.PUT)
    public ResponseEntity<?> removeParticipantFromMeeting(
            @PathVariable("id") long id,
            @RequestBody Participant participantToRemove) {

        Participant participant = participantService.findByLogin(participantToRemove.getLogin());
        Collection<Participant> participants = meetingService.getEnrolled(id);

        if (participant == null) {
            return new ResponseEntity("A participant with login " + participantToRemove.getLogin()
                    + " does not exist.", HttpStatus.NOT_FOUND);

        } else if (!participants.contains(participant)) {
            return new ResponseEntity("A participant with login " + participantToRemove.getLogin()
                    + " was not register for this meeting", HttpStatus.NOT_FOUND);
        } else {
            meetingService.deleteParticipantToMeeting(id, participant);
            return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
        }
    }

}



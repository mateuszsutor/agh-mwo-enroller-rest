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
    public ResponseEntity<?> registerMeeting(@RequestBody Meeting meeting) {
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
        Meeting meeting = meetingService.findByIdMeeting(id);

        return new ResponseEntity<Collection<Participant>>(meeting.getParticipants(), HttpStatus.ACCEPTED);
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
}



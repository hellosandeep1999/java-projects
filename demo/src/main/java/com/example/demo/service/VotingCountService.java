package com.example.demo.service;

import com.example.demo.domain.model.*;
import com.example.demo.domain.model.enums.SelectionType;
import com.example.demo.generated.jooq.Tables;
import com.example.demo.generated.jooq.enums.CandidateAndVotingSelectiontype;
import com.example.demo.generated.jooq.enums.VotersAuthorizedIsvoted;
import com.example.demo.generated.jooq.tables.records.PositionsRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.tags.form.SelectTag;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VotingCountService {

    @Autowired
    DSLContext dslContext;


    public void submitVote(VotingRequest votingRequest) {
        var ids = votingRequest.voteByPositions().stream().map(VoteByPosition::id).toList();
        if(!ids.isEmpty()){
            dslContext.update(Tables.CANDIDATE_AND_VOTING)
                    .set(Tables.CANDIDATE_AND_VOTING.VOTECOUNT, Tables.CANDIDATE_AND_VOTING.VOTECOUNT.add(1))
                    .where(Tables.CANDIDATE_AND_VOTING.ID.in(ids))
                    .execute();
        }
        var response = convertEmployeeToJson(votingRequest);
        dslContext.update(Tables.VOTERS_AUTHORIZED)
                .set(Tables.VOTERS_AUTHORIZED.ISVOTED, VotersAuthorizedIsvoted.VOTED)
                .set(Tables.VOTERS_AUTHORIZED.VOTINGRESPONSE,response)
                .where(Tables.VOTERS_AUTHORIZED.LOGINKEY.eq(votingRequest.loginKey())).execute();
    }


    public static String convertEmployeeToJson(VotingRequest votingRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(votingRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public VoteCountResponse getVotingCountByCategory() {
        var data =  dslContext.selectFrom(Tables.CANDIDATE_AND_VOTING)
                .fetchInto(Tables.CANDIDATE_AND_VOTING).stream().map(record->
                new VotingCountDao(record.get(Tables.CANDIDATE_AND_VOTING.NAME),
                        record.get(Tables.CANDIDATE_AND_VOTING.POSITION),
                        record.get(Tables.CANDIDATE_AND_VOTING.VOTECOUNT))).toList();

        Map<String, List<VotingCountDao>> map = data.stream().collect(Collectors.groupingBy(VotingCountDao::positionName));

        var candidateVoteCount = map.entrySet().stream().map(entry->{
                    var candidateCount = entry.getValue().stream().map(e-> new CandidateVoteCount(e.candidateName(),e.count())).toList();
                    return new PositionByCount(entry.getKey(), candidateCount);
                }
                ).toList();
        return new VoteCountResponse(candidateVoteCount);
    }

    public List<PositionCandidate> getVotingDataForVoting(String loginKey) {
       var voterData =  dslContext.selectFrom(Tables.VOTERS_AUTHORIZED)
               .where(Tables.VOTERS_AUTHORIZED.LOGINKEY.eq(loginKey))
                .fetchInto(Tables.VOTERS_AUTHORIZED).map(record->
                        new Voter(0L,
                                record.get(Tables.VOTERS_AUTHORIZED.SERIALNUMBER),
                                record.get(Tables.VOTERS_AUTHORIZED.NAME),
                                record.get(Tables.VOTERS_AUTHORIZED.DESIGNATION),
                                record.get(Tables.VOTERS_AUTHORIZED.ORGANIZATION),
                                record.get(Tables.VOTERS_AUTHORIZED.PLACEOFPOSTING),
                                record.get(Tables.VOTERS_AUTHORIZED.ZONE),
                                record.get(Tables.VOTERS_AUTHORIZED.STATE),
                                record.get( Tables.VOTERS_AUTHORIZED.MOBILENUMBER),
                                record.get( Tables.VOTERS_AUTHORIZED.EMAIL),
                                null
                        ));
        var positionVsPositionId = dslContext.selectFrom(Tables.POSITIONS)
                .fetch().stream().collect(Collectors.toMap(PositionsRecord::component2, PositionsRecord::component1));

        var eligibleCandidates = getEligibleCandidates(voterData.get(0));
        Map<String, List<VoteByPosition>> positionByCandidates = eligibleCandidates.stream()
                .collect(Collectors.groupingBy(VoteByPosition::positionName));

        var sortedMap = positionByCandidates.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparingLong(positionVsPositionId::get)))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new // Preserve insertion order
                ));
        return sortedMap.entrySet().stream().map(entry->{
                    var candidates = entry.getValue().stream().map(e->
                            new ChooseVote(e.id(),e.CandidateName())).toList();
                    return new PositionCandidate(entry.getKey(), candidates);
                }
        ).toList();
    }

    private List<VoteByPosition> getEligibleCandidates(Voter voterData) {
       var voteByPositions = new ArrayList<VoteByPosition>();

       var allSelectionTypes = SelectionType.class.getEnumConstants();
       for(SelectionType selectionType : allSelectionTypes){
          var record =  switch (selectionType){
               case ALL -> dslContext.selectFrom(Tables.CANDIDATE_AND_VOTING)
                       .where(Tables.CANDIDATE_AND_VOTING.SELECTIONTYPE.eq(CandidateAndVotingSelectiontype.ALL))
                       .fetchInto(Tables.CANDIDATE_AND_VOTING);

               case ORG -> dslContext.selectFrom(Tables.CANDIDATE_AND_VOTING)
                       .where(Tables.CANDIDATE_AND_VOTING.SELECTIONTYPE.eq(CandidateAndVotingSelectiontype.ORG)
                               .and(Tables.CANDIDATE_AND_VOTING.ORGANIZATION.eq(voterData.organization())))
                       .fetchInto(Tables.CANDIDATE_AND_VOTING);

               case ZONE -> dslContext.selectFrom(Tables.CANDIDATE_AND_VOTING)
                       .where(Tables.CANDIDATE_AND_VOTING.SELECTIONTYPE.eq(CandidateAndVotingSelectiontype.ZONE)
                               .and(Tables.CANDIDATE_AND_VOTING.ZONE.eq(voterData.zone())))
                       .fetchInto(Tables.CANDIDATE_AND_VOTING);

               case STATE -> dslContext.selectFrom(Tables.CANDIDATE_AND_VOTING)
                       .where(Tables.CANDIDATE_AND_VOTING.SELECTIONTYPE.eq(CandidateAndVotingSelectiontype.STATE)
                               .and(Tables.CANDIDATE_AND_VOTING.STATE.eq(voterData.state())))
                       .fetchInto(Tables.CANDIDATE_AND_VOTING);

               case ORG_ZONE -> dslContext.selectFrom(Tables.CANDIDATE_AND_VOTING)
                       .where(Tables.CANDIDATE_AND_VOTING.SELECTIONTYPE.eq(CandidateAndVotingSelectiontype.ORG_ZONE)
                               .and(Tables.CANDIDATE_AND_VOTING.ZONE.eq(voterData.zone())
                                       .and(Tables.CANDIDATE_AND_VOTING.ORGANIZATION.eq(voterData.organization()))))
                       .fetchInto(Tables.CANDIDATE_AND_VOTING);

          };
          var voteByPosition = record.stream().map(r->new VoteByPosition(r.get(Tables.CANDIDATE_AND_VOTING.ID),
                  r.get(Tables.CANDIDATE_AND_VOTING.POSITION),
                  r.get(Tables.CANDIDATE_AND_VOTING.NAME))).toList();
           voteByPositions.addAll(voteByPosition);
       }
       return voteByPositions;
    }

}

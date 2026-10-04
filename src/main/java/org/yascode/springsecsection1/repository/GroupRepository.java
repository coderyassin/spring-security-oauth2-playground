package org.yascode.springsecsection1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yascode.springsecsection1.model.Group;
import org.yascode.springsecsection1.repository.projection.GroupNameView;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<GroupNameView> findGroupNamesBy();

    Optional<Group> findByGroupName(String groupName);

    @Query("""
            select member.username
            from Group g
            join g.members member
            where g.groupName = :groupName
            """)
    List<String> findMemberUsernamesByGroupName(@Param("groupName") String groupName);

}


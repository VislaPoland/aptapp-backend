package com.creatix.domain.entity.store.community.board;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Tomas Michalek on 10/05/2017.
 */
@Data
@Entity
public class CommunityBoardCategory {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    String title;

}

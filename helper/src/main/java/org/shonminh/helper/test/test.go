package model

type TestTab struct{
		Id           uint64  `gorm:"type:BIGINT (21) UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;NOT NULL"`
		UserId       uint32  `gorm:"type:INT (11) UNSIGNED;NOT NULL"`
		ParentUserId string  `gorm:"type:VARCHAR (64);NOT NULL"`
		NoId         int64   `gorm:"type:BIGINT (21);NOT NULL"`
		Tinyname     string  `gorm:"type:VARCHAR (64);NOT NULL"`
		Score        float64 `gorm:"type:DECIMAL (10, 3);NOT NULL"`
		CreateTime   uint32  `gorm:"type:INT (11) UNSIGNED;NOT NULL"`
		UpdateTime   uint32  `gorm:"type:INT (11) UNSIGNED;NOT NULL"`
}


package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*

class KeywordISpec extends grails.plugin.spock.IntegrationSpec {
	private static simpleActivity
	def setup() {
		simpleActivity = Announcement.build(name:'whatever', deleted: false, archived: false)
	}
	
	@Unroll
	def "Keyword must have a value and an Activity"() {
		given:
			def k = new Keyword(value:word, isTopLevel:true)
			if(hasActivity)
				simpleActivity.addToKeywords(k)
		expect:
			k.validate() == valid
		where:
			word   | hasActivity     | valid
			null   | false           | false
			null   | true            | false
			'TEST' | false           | false
			'T3ST' | true            | true
	}

	@Unroll
	def "top level keyword must be unique unless keyword with same's activity name is archived or deleted"() {
		given:
			def k1 = new Keyword(value:keyword, isTopLevel: true)
		when:
			new Autoreply(name:'whatever1', autoreplyText:'1', archived:k1archived, deleted:k1deleted)
				.addToKeywords(k1)
				.save(flush:true, failOnError:true)

		then:
			k1.validate()
		when:
			def k2 = new Keyword(value:keyword, isTopLevel: true)
			new Autoreply(name:'whatever2', autoreplyText:'2')
				.addToKeywords(k2)
		then:
			k2.validate() == k2valid
		where:
			keyword | k1archived | k1deleted | k2valid
			''      | false      | false     | false
			''      | true       | false     | true
			''      | false      | true      | true
			''      | true       | true      | true
			'LOCK'  | false      | false     | false
			'LOCK'  | true       | false     | true
			'LOCK'  | false      | true      | true
			'LOCK'  | true       | true      | true
	}

	@Unroll
	def "non-top level keyword must be unique within the activity"() {
		given:
			def k1 = new Keyword(value:keyword, isTopLevel: false, ownerDetail:'SOMETHING')
			def k2 = new Keyword(value:keyword2, isTopLevel: false, ownerDetail:'SOMETHING-ELSE')
		when:
			new Autoreply(name:'whatever3', autoreplyText:'2')
				.addToKeywords(k1)
				.save(failOnError:true)
				.addToKeywords(k2)
		then:
			k2.validate() == valid
		where:
			keyword | keyword2   | valid
			''      | ''         | false
			''      | 'LOCK'     | true
			'LOCK'  | ''         | true
			'LOCK'  | 'LOCK'     | false
	}

	@Unroll
	def "non-top level keywords can share value if they don't have the same activity"() {
		given:
			def k1 = new Keyword(value:keyword, isTopLevel: false, ownerDetail:'SOMETHING')
			def k2 = new Keyword(value:keyword2, isTopLevel: false, ownerDetail:'SOMETHING-ELSE')
		when:
			new Autoreply(name:'whatever4', autoreplyText:'2')
				.addToKeywords(k1)
				.save(flush:true, failOnError:true)
			new Autoreply(name:'whatever5', autoreplyText:'2')
				.addToKeywords(k2)
				.save(flush:true, failOnError:true)
		then:
			k2.validate() == valid
		where:
			keyword | keyword2   | valid
			''      | ''         | true
			''      | 'LOCK'     | true
			'LOCK'  | ''         | true
			'LOCK'  | 'LOCK'     | true
	}

}


package com.example.chords2.data

import com.example.chords2.data.database.song.SongEntity

object TestData {
    val songs = listOf(
        SongEntity(
            title = "Song 1",
            artist = "Artist 1",
            content = """
                Jó ulice

                [Ami]Jeho otec se sebou měl problému dost,
                [Dmi]a v bohatý rodině taky [C]nevyrost.
                [Ami]Vlastní matka nevěděla, jestli ho má ráda,
                [E]stalo se co muselo, [Ami]když ukázal jim záda.
                [Ami]Problémovej parchant furt by si jen hrál,
                [Dmi]učit se mu nechtělo, už ve [C]školce se rval
                [Ami]a když pořád slyšel co tu chce ten šmejd,
                [E]jediný ho napadlo, [Ami]mezi svejma bejt.

                [F]Jó, uli[C]ce je [E]jako sen[F],
                [F]duše v plame[C]nech, děsně [E]dlouhej [Ami]den.
                Jó, ulice, teď patřej sem,
                noc je královna, no problem!

                Tak poprvé v životě našel kamaráda,
                s ním si nechal na rameno vytetovat hada.
                Spolu to snad jednou někam dotáhnou,
                když na těžkejch křižovatkách blbě nezahnou.

                Hodně lidí spojil, jak ocel tvrdej rock.
                Jsou démoni ulic a pro vostatní šok.
                A chtěli by i milovat, o nic hůř než vy,
                na ulici léčej si svý duše bolavý.

                Jó, ulice...

                Někdy je i nejhůř, krev často všechno zkazí.
                Vopruz je když jeden do druhýho nůž vrazí.
                Jinde zase večer slzy provázej,
                to mladí lidi s báglama z domu odcházej.

                Jó, ulice...
            """.trimIndent(),
            hBFormat = com.example.chords2.data.model.util.HBFormat.ENG
        ),
        SongEntity(
            title = "Antos",
            artist = "Totalni nasazeni",
            content = """
                Mělo [G]český království
                ve všech městech to se ví
                svoje pivovary každý střežil
                [D]svoje tajemství.
                Slaný bylo město měst
                křižovatkou hlavních cest
                léta páně roku
                jeden tisíc [G]pětset třicet šest.


                Měšťan Antoš za to vzal
                svoje pivo vařit dal
                mezi domy s právem
                várečným byl
                zdejší pivní král.
                Tenhle věhlas přetrval
                v knihách starých o tom psal
                slavný jezuita Balbín
                co to sdílel v Čechách dál.

                Ref:
                [Em]Je to pivo z města Slaný
                [C]je to pivo požehnaný
                [G]je to pivo co je víc
                než co je [D]pocit rozkoše
                [Em]je to pivo co je živý
                [C]je to pivo mezi pivy
                [G]je to naše slánský pivo
                z pivo[D]varu Antoše.

                Jenže pak zákazy shora
                Martinic a Bílá Hora
                přesto pivo vařil sládek Poupě
                byl tu za nestora
                nastal ale s vodou svízel
                náš pivovar s krizí zmizel
                a pak sto pětatřicet let
                slánský pivo nenabízel.

                Až pak v dalším století
                prolomili prokletí
                slánský Antoš znovu ožil
                s novou pivní pečetí.
                Žádnej obyčejnej bar
                ale slánskej pivovar
                tam je to nejlepší pivo
                Antoš česká pivní stár.

                3x Ref.

            """.trimIndent(),
            hBFormat = com.example.chords2.data.model.util.HBFormat.GER
        )
    )
}
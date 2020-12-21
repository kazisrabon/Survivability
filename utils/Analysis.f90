Program Survivability

    parameter (generator=2,load=2,link=2,nm=generator+load+link)
    integer(8) m, nt, in, Totalpower, Actualpower, l, nc, SNM
    integer(8) X(nm), P(nm), E(nm), F(nm), S(nm), R(nm), N(nm)
    integer(8) Y(nm, nm), Z(nm, nm), O(nm), C(nm)
    real(8)    PS, PR, PF, survived, reconf, failed, totalCase

    Y = 0
    call topology(nm, Y)

    do i = 1, nm
        E(i) = 0
        if(i<=generator) E(i) = 1
        if(i>generator.and.i<=generator + load) E(i) = -1
    enddo

    Totalpower = generator

    nt = 2**nm
    F = 0
    S = 0
    R = 0
    N = 0

    do k = 1, nt
        write(*, *) 'nt=', nt, 'k=', k
        P = 0
        in = k - 1

        Actualpower = Totalpower

        do i = 1, nm
            l = in / 2
            P(i) = in - 2 * l
            in = l
        enddo

        m = 0
        do i = 1, nm
            if(P(i)==0)m = m + 1
        enddo
        if(m==0) goto 5

        Z = 0
        do i = 1, nm
            do j = 1, nm
                Z(i, j) = Y(i, j) * P(i) * P(j)
            enddo
        enddo

        do i = 1, generator
            if(P(i)==0) then
                Actualpower = Actualpower - E(i)
                goto 1
            endif

            nc = 0
            C = 0
            O = 0

            j1 = 1
            C(j1) = i
            k1 = i
            j2 = 0

            3   continue

            do k2 = 1, nm
                do jj1 = 1, nm
                    if(k2==C(jj1).or.k2==O(jj1)) goto 2
                enddo

                if(Z(k1, k2)==1)then

                    if(k1>=generator + 1.and.k1<=generator + load)then
                        nc = 1
                        goto 1
                    endif
                    if(k2>=generator + 1.and.k2<=generator + load)then
                        nc = 1
                        goto 1
                    endif

                    j2 = j2 + 1
                    O(j2) = k2
                endif

            2      continue
            end do

            if(j2>0)then
                k1 = O(j2)
                j1 = j1 + 1
                C(j1) = O(j2)
                O(j2) = 0
                j2 = j2 - 1
                goto 3
            endif

            if(nc==0)    Actualpower = Actualpower - E(i)
        1   continue
        end do

        if(Actualpower==Totalpower) S(m) = S(m) + 1
        if(Actualpower==0) F(m) = F(m) + 1
        if(Actualpower>0.and.Actualpower<Totalpower) R(m) = R(m) + 1

        N(m) = N(m) + 1

    5   continue
    end do

    SNM = 1
    do m = 1, nm
        if(S(m) + R(m) + F(m)/=N(m)) then
            write(*, *) 'ERROR at m=', m
        endif
        SNM = N(m) + SNM
    enddo
    if(SNM/=nt) then
        write(*, *) 'ERROR'
    endif

    open(14, File = 'Scenarios.dat')
    write(14, *) 'm:S,R,F,N'

    open(15, File = 'Probabilities.dat')
    write(15, *) 'm:P(S),P(R),P(F)'

    do m = 1, nm
        survived = S(m)
        reconf = R(m)
        failed = F(m)
        totalCase = N(m)
        PS = survived / totalCase
        PR = reconf / totalCase
        PF = failed / totalCase
        write(14, 92) m, S(m), R(m), F(m), N(m)
        write(15, 93) m, PS, PR, PF
    enddo

    close(14)
    close(15)

    92  format(i4, 4i20)
    93  format(i4, 3f10.3)

    stop
end

subroutine topology(nm, Y)
    integer(8) Y(nm, nm)

    Y(1,3)=1
    Y(1,5)=1
    Y(1,6)=1
    Y(2,4)=1
    Y(2,5)=1
    Y(2,6)=1
    Y(3,1)=1
    Y(3,5)=1
    Y(3,6)=1
    Y(4,2)=1
    Y(4,5)=1
    Y(4,6)=1
    Y(5,1)=1
    Y(6,1)=1
    Y(5,2)=1
    Y(6,2)=1
    Y(5,3)=1
    Y(6,3)=1
    Y(5,4)=1
    Y(6,4)=1
    Y(5,6)=1
    Y(6,5)=1
    do j = 1, nm
        do i = j + 1, nm
            Y(i, j) = Y(j, i)
        enddo
    enddo

    return
end